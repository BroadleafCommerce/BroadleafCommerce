import type {
  FulfillmentItemInput,
  FulfillmentOptionInput,
  ShippingEstimateRequest,
  ValidationErrorField,
} from "../contracts/shipping-estimate.js";
import { isDecimalString } from "./money.js";

const OPTION_TYPES = new Set(["FIXED_PRICE", "BANDED_PRICE", "BANDED_WEIGHT"]);
const RESULT_AMOUNT_TYPES = new Set(["RATE", "PERCENTAGE"]);
const WEIGHT_UNITS = new Set(["POUNDS", "OUNCES", "KILOGRAMS"]);

export interface ParsedShippingEstimateRequest {
  request?: ShippingEstimateRequest;
  fields: ValidationErrorField[];
}

export function parseShippingEstimateRequest(input: unknown): ParsedShippingEstimateRequest {
  const fields: ValidationErrorField[] = [];

  if (!isRecord(input)) {
    return {
      fields: [
        {
          path: "$",
          code: "INVALID_TYPE",
          message: "Request body must be a JSON object.",
        },
      ],
    };
  }

  validateCurrency(input.currency, fields);
  validateItems(input.fulfillmentGroup, fields);
  validateOptions(input.options, fields);

  const fulfillmentGroup = input.fulfillmentGroup;
  const items = isRecord(fulfillmentGroup) ? fulfillmentGroup.items : undefined;

  if (Array.isArray(items) && Array.isArray(input.options)) {
    validateOptionItemRequirements(
      items as FulfillmentItemInput[],
      input.options as FulfillmentOptionInput[],
      fields,
    );
  }

  return {
    request: fields.length === 0 ? (input as unknown as ShippingEstimateRequest) : undefined,
    fields,
  };
}

function validateCurrency(value: unknown, fields: ValidationErrorField[]): void {
  if (typeof value !== "string" || !/^[A-Z]{3}$/.test(value)) {
    fields.push({
      path: "currency",
      code: "INVALID",
      message: "Currency must be a three-letter uppercase code.",
    });
  }
}

function validateItems(value: unknown, fields: ValidationErrorField[]): void {
  if (!isRecord(value) || !Array.isArray(value.items)) {
    fields.push({
      path: "fulfillmentGroup.items",
      code: "REQUIRED",
      message: "Fulfillment group items are required.",
    });
    return;
  }

  value.items.forEach((item, index) => {
    const path = `fulfillmentGroup.items[${index}]`;

    if (!isRecord(item)) {
      fields.push({
        path,
        code: "INVALID_TYPE",
        message: "Fulfillment item must be an object.",
      });
      return;
    }

    if (typeof item.itemId !== "string" || item.itemId.length === 0) {
      fields.push({
        path: `${path}.itemId`,
        code: "REQUIRED",
        message: "Item id is required.",
      });
    }

    if (typeof item.quantity !== "number" || !Number.isInteger(item.quantity) || item.quantity <= 0) {
      fields.push({
        path: `${path}.quantity`,
        code: "INVALID",
        message: "Quantity must be a positive integer.",
      });
    }

    validateOptionalDecimal(item.totalItemAmount, `${path}.totalItemAmount`, fields);
    validateOptionalDecimal(item.unitPrice, `${path}.unitPrice`, fields);

    if (item.weight !== undefined) {
      validateWeight(item.weight, `${path}.weight`, fields);
    }

    if (item.flatRates !== undefined) {
      validateFlatRates(item.flatRates, `${path}.flatRates`, fields);
    }
  });
}

function validateOptions(value: unknown, fields: ValidationErrorField[]): void {
  if (!Array.isArray(value) || value.length === 0) {
    fields.push({
      path: "options",
      code: "REQUIRED",
      message: "At least one fulfillment option is required.",
    });
    return;
  }

  const optionIds = new Set<string>();

  value.forEach((option, index) => {
    const path = `options[${index}]`;

    if (!isRecord(option)) {
      fields.push({
        path,
        code: "INVALID_TYPE",
        message: "Fulfillment option must be an object.",
      });
      return;
    }

    if (typeof option.optionId !== "string" || option.optionId.length === 0) {
      fields.push({
        path: `${path}.optionId`,
        code: "REQUIRED",
        message: "Fulfillment option id is required.",
      });
    } else if (optionIds.has(option.optionId)) {
      fields.push({
        path: `${path}.optionId`,
        code: "DUPLICATE",
        message: "Fulfillment option ids must be unique.",
      });
    } else {
      optionIds.add(option.optionId);
    }

    if (typeof option.name !== "undefined" && typeof option.name !== "string") {
      fields.push({
        path: `${path}.name`,
        code: "INVALID_TYPE",
        message: "Fulfillment option name must be a string.",
      });
    }

    if (typeof option.type !== "string" || !OPTION_TYPES.has(option.type)) {
      fields.push({
        path: `${path}.type`,
        code: "INVALID",
        message: "Fulfillment option type is not supported.",
      });
      return;
    }

    if (option.type === "FIXED_PRICE") {
      validateRequiredDecimal(option.price, `${path}.price`, fields);
      return;
    }

    validateBands(option.bands, option.type, path, fields);
  });
}

function validateBands(
  value: unknown,
  optionType: unknown,
  optionPath: string,
  fields: ValidationErrorField[],
): void {
  if (!Array.isArray(value) || value.length === 0) {
    fields.push({
      path: `${optionPath}.bands`,
      code: "REQUIRED",
      message: "Banded fulfillment options require at least one band.",
    });
    return;
  }

  value.forEach((band, index) => {
    const path = `${optionPath}.bands[${index}]`;

    if (!isRecord(band)) {
      fields.push({
        path,
        code: "INVALID_TYPE",
        message: "Fulfillment band must be an object.",
      });
      return;
    }

    if (optionType === "BANDED_PRICE") {
      validateRequiredDecimal(band.minimumAmount, `${path}.minimumAmount`, fields);
    } else {
      validateRequiredDecimal(band.minimumWeight, `${path}.minimumWeight`, fields);
    }

    validateRequiredDecimal(band.resultAmount, `${path}.resultAmount`, fields);

    if (typeof band.resultAmountType !== "string" || !RESULT_AMOUNT_TYPES.has(band.resultAmountType)) {
      fields.push({
        path: `${path}.resultAmountType`,
        code: "INVALID",
        message: "Band result amount type must be RATE or PERCENTAGE.",
      });
    }
  });
}

function validateOptionItemRequirements(
  items: FulfillmentItemInput[],
  options: FulfillmentOptionInput[],
  fields: ValidationErrorField[],
): void {
  options.forEach((option, optionIndex) => {
    if (!isRecord(option) || typeof option.optionId !== "string") {
      return;
    }

    items.forEach((item, itemIndex) => {
      if (!isRecord(item) || hasFlatRateForOption(item, option.optionId)) {
        return;
      }

      if (option.type === "BANDED_PRICE" && !item.totalItemAmount && !item.unitPrice) {
        fields.push({
          path: `fulfillmentGroup.items[${itemIndex}]`,
          code: "REQUIRED",
          message: `Item requires totalItemAmount or unitPrice for option ${option.optionId}.`,
        });
      }

      if (option.type === "BANDED_WEIGHT" && item.weight === undefined) {
        fields.push({
          path: `fulfillmentGroup.items[${itemIndex}].weight`,
          code: "REQUIRED",
          message: `Item weight is required for option ${option.optionId}.`,
        });
      }
    });

    if (option.type === "BANDED_PRICE" || option.type === "BANDED_WEIGHT") {
      validateConfiguredBands(option, `options[${optionIndex}]`, fields);
    }
  });
}

function validateConfiguredBands(
  option: FulfillmentOptionInput,
  path: string,
  fields: ValidationErrorField[],
): void {
  if (!("bands" in option) || !Array.isArray(option.bands)) {
    return;
  }

  option.bands.forEach((band, index) => {
    if (!isRecord(band)) {
      return;
    }

    if (band.resultAmountType === "PERCENTAGE" && option.type === "BANDED_WEIGHT") {
      fields.push({
        path: `${path}.bands[${index}].resultAmountType`,
        code: "UNSUPPORTED",
        message: "Percentage weight bands are not part of the first extraction slice.",
      });
    }
  });
}

function validateWeight(value: unknown, path: string, fields: ValidationErrorField[]): void {
  if (!isRecord(value)) {
    fields.push({
      path,
      code: "INVALID_TYPE",
      message: "Weight must be an object.",
    });
    return;
  }

  validateRequiredDecimal(value.amount, `${path}.amount`, fields);

  if (typeof value.unit !== "string" || !WEIGHT_UNITS.has(value.unit)) {
    fields.push({
      path: `${path}.unit`,
      code: "INVALID",
      message: "Weight unit must be POUNDS, OUNCES, or KILOGRAMS.",
    });
  }
}

function validateFlatRates(value: unknown, path: string, fields: ValidationErrorField[]): void {
  if (!Array.isArray(value)) {
    fields.push({
      path,
      code: "INVALID_TYPE",
      message: "Flat rates must be an array.",
    });
    return;
  }

  value.forEach((flatRate, index) => {
    const flatRatePath = `${path}[${index}]`;

    if (!isRecord(flatRate)) {
      fields.push({
        path: flatRatePath,
        code: "INVALID_TYPE",
        message: "Flat rate must be an object.",
      });
      return;
    }

    if (typeof flatRate.optionId !== "string" || flatRate.optionId.length === 0) {
      fields.push({
        path: `${flatRatePath}.optionId`,
        code: "REQUIRED",
        message: "Flat rate option id is required.",
      });
    }

    validateRequiredDecimal(flatRate.amount, `${flatRatePath}.amount`, fields);
  });
}

function validateOptionalDecimal(
  value: unknown,
  path: string,
  fields: ValidationErrorField[],
): void {
  if (value !== undefined) {
    validateRequiredDecimal(value, path, fields);
  }
}

function validateRequiredDecimal(
  value: unknown,
  path: string,
  fields: ValidationErrorField[],
): void {
  if (!isDecimalString(value)) {
    fields.push({
      path,
      code: "INVALID",
      message: "Value must be a non-negative decimal string.",
    });
  }
}

function hasFlatRateForOption(item: FulfillmentItemInput, optionId: string): boolean {
  return item.flatRates?.some((flatRate) => flatRate.optionId === optionId) ?? false;
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}
