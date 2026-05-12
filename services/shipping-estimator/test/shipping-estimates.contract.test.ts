import assert from "node:assert/strict";
import { after, before, describe, it } from "node:test";
import type { AddressInfo } from "node:net";
import { createApp } from "../src/app.js";

describe("shipping estimate contract", () => {
  const app = createApp();
  let baseUrl: string;

  before(async () => {
    await new Promise<void>((resolve) => {
      app.listen(0, "127.0.0.1", resolve);
    });

    const address = app.address() as AddressInfo;
    baseUrl = `http://${address.address}:${address.port}`;
  });

  after(async () => {
    await new Promise<void>((resolve, reject) => {
      app.close((error) => {
        if (error) {
          reject(error);
          return;
        }

        resolve();
      });
    });
  });

  it("prices the legacy banded-price happy path by selecting the highest qualifying band", async () => {
    const response = await postEstimate(baseUrl, {
      currency: "USD",
      fulfillmentGroup: {
        items: [
          {
            itemId: "line-1",
            quantity: 1,
            totalItemAmount: "25.00",
          },
        ],
      },
      options: [
        {
          optionId: "standard",
          name: "Standard",
          type: "BANDED_PRICE",
          bands: [
            { minimumAmount: "10.00", resultAmount: "10.00", resultAmountType: "RATE" },
            { minimumAmount: "20.00", resultAmount: "20.00", resultAmountType: "RATE" },
            { minimumAmount: "30.00", resultAmount: "30.00", resultAmountType: "RATE" },
          ],
        },
      ],
    });

    assert.equal(response.status, 200);
    const body = await response.json();

    assert.equal(body.currency, "USD");
    assert.equal(body.estimates[0].optionId, "standard");
    assert.equal(body.estimates[0].amount, "20.00");
    assert.equal(body.estimates[0].explanation.basisTotal, "25.00");
    assert.equal(body.estimates[0].explanation.matchedBand.minimum, "20.00");
  });

  it("returns a validation error when a banded option has no bands", async () => {
    const response = await postEstimate(baseUrl, {
      currency: "USD",
      fulfillmentGroup: {
        items: [
          {
            itemId: "line-1",
            quantity: 1,
            totalItemAmount: "25.00",
          },
        ],
      },
      options: [
        {
          optionId: "standard",
          type: "BANDED_PRICE",
          bands: [],
        },
      ],
    });

    assert.equal(response.status, 422);
    const body = await response.json();

    assert.equal(body.error.code, "VALIDATION_ERROR");
    assert.equal(body.error.fields[0].path, "options[0].bands");
    assert.equal(body.error.fields[0].code, "REQUIRED");
  });

  it("keeps the legacy duplicate-minimum behavior by choosing the cheapest result", async () => {
    const response = await postEstimate(baseUrl, {
      currency: "USD",
      fulfillmentGroup: {
        items: [
          {
            itemId: "line-1",
            quantity: 2,
            unitPrice: "5.00",
          },
        ],
      },
      options: [
        {
          optionId: "standard",
          type: "BANDED_PRICE",
          bands: [
            { minimumAmount: "10.00", resultAmount: "30.00", resultAmountType: "RATE" },
            { minimumAmount: "10.00", resultAmount: "20.00", resultAmountType: "RATE" },
            { minimumAmount: "10.00", resultAmount: "10.00", resultAmountType: "RATE" },
          ],
        },
      ],
    });

    assert.equal(response.status, 200);
    const body = await response.json();

    assert.equal(body.estimates[0].amount, "10.00");
    assert.equal(body.estimates[0].explanation.matchedBand.minimum, "10.00");
    assert.equal(body.estimates[0].explanation.matchedBand.resultAmount, "10.00");
    assert.match(body.estimates[0].explanation.notes.join(" "), /Duplicate band minimums/);
  });
});

async function postEstimate(baseUrl: string, body: unknown): Promise<Response> {
  return fetch(`${baseUrl}/shipping-estimates`, {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify(body),
  });
}
