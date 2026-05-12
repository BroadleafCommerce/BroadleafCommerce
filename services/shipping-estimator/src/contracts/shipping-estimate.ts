export type FulfillmentOptionType = "FIXED_PRICE" | "BANDED_PRICE" | "BANDED_WEIGHT";

export type FulfillmentBandResultAmountType = "RATE" | "PERCENTAGE";

export type WeightUnit = "POUNDS" | "OUNCES" | "KILOGRAMS";

export interface ShippingEstimateRequest {
  currency: string;
  fulfillmentGroup: {
    items: FulfillmentItemInput[];
  };
  options: FulfillmentOptionInput[];
}

export interface FulfillmentItemInput {
  itemId: string;
  quantity: number;
  totalItemAmount?: string;
  unitPrice?: string;
  weight?: {
    amount: string;
    unit: WeightUnit;
  };
  flatRates?: FulfillmentFlatRateInput[];
}

export interface FulfillmentFlatRateInput {
  optionId: string;
  amount: string;
}

export type FulfillmentOptionInput =
  | FixedPriceFulfillmentOptionInput
  | BandedPriceFulfillmentOptionInput
  | BandedWeightFulfillmentOptionInput;

export interface BaseFulfillmentOptionInput {
  optionId: string;
  name?: string;
  type: FulfillmentOptionType;
  useFlatRates?: boolean;
}

export interface FixedPriceFulfillmentOptionInput extends BaseFulfillmentOptionInput {
  type: "FIXED_PRICE";
  price: string;
}

export interface BandedPriceFulfillmentOptionInput extends BaseFulfillmentOptionInput {
  type: "BANDED_PRICE";
  bands: PriceFulfillmentBandInput[];
}

export interface BandedWeightFulfillmentOptionInput extends BaseFulfillmentOptionInput {
  type: "BANDED_WEIGHT";
  bands: WeightFulfillmentBandInput[];
}

export interface PriceFulfillmentBandInput {
  minimumAmount: string;
  resultAmount: string;
  resultAmountType: FulfillmentBandResultAmountType;
}

export interface WeightFulfillmentBandInput {
  minimumWeight: string;
  resultAmount: string;
  resultAmountType: FulfillmentBandResultAmountType;
}

export interface ShippingEstimateResponse {
  currency: string;
  estimates: ShippingEstimate[];
}

export interface ShippingEstimate {
  optionId: string;
  name?: string;
  amount: string;
  explanation: ShippingEstimateExplanation;
}

export interface ShippingEstimateExplanation {
  strategy: FulfillmentOptionType;
  basisTotal: string;
  flatRateTotal: string;
  matchedBand?: {
    minimum: string;
    resultAmount: string;
    resultAmountType: FulfillmentBandResultAmountType;
  };
  notes: string[];
}

export interface ValidationErrorField {
  path: string;
  code: string;
  message: string;
}

export interface ErrorResponse {
  error: {
    code: string;
    message: string;
    fields?: ValidationErrorField[];
  };
}
