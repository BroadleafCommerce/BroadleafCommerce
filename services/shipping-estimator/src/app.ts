import { createServer, type IncomingMessage, type ServerResponse } from "node:http";
import type { ErrorResponse } from "./contracts/shipping-estimate.js";
import { estimateShipping } from "./domain/shipping-estimator.js";
import { parseShippingEstimateRequest } from "./domain/validate-shipping-estimate.js";

const MAX_BODY_BYTES = 1_000_000;

export function createApp() {
  return createServer(async (request, response) => {
    try {
      await handleRequest(request, response);
    } catch (error) {
      writeJson(response, 500, {
        error: {
          code: "INTERNAL_ERROR",
          message: error instanceof Error ? error.message : "Unexpected shipping estimator error.",
        },
      });
    }
  });
}

async function handleRequest(request: IncomingMessage, response: ServerResponse): Promise<void> {
  const method = request.method ?? "GET";
  const url = new URL(request.url ?? "/", "http://localhost");

  if (method === "GET" && url.pathname === "/health") {
    writeJson(response, 200, { status: "ok", service: "shipping-estimator" });
    return;
  }

  if (method === "POST" && url.pathname === "/shipping-estimates") {
    const body = await readJsonBody(request);

    if (!body.ok) {
      writeJson(response, body.statusCode, body.response);
      return;
    }

    const parsedRequest = parseShippingEstimateRequest(body.value);

    if (!parsedRequest.request) {
      writeJson(response, 422, {
        error: {
          code: "VALIDATION_ERROR",
          message: "Shipping estimate request failed validation.",
          fields: parsedRequest.fields,
        },
      });
      return;
    }

    writeJson(response, 200, estimateShipping(parsedRequest.request));
    return;
  }

  writeJson(response, 404, {
    error: {
      code: "NOT_FOUND",
      message: "Route not found.",
    },
  });
}

type ReadJsonResult =
  | {
      ok: true;
      value: unknown;
    }
  | {
      ok: false;
      statusCode: number;
      response: ErrorResponse;
    };

async function readJsonBody(request: IncomingMessage): Promise<ReadJsonResult> {
  const chunks: Buffer[] = [];
  let totalBytes = 0;

  for await (const chunk of request) {
    const buffer = Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk);
    totalBytes += buffer.byteLength;

    if (totalBytes > MAX_BODY_BYTES) {
      return {
        ok: false,
        statusCode: 413,
        response: {
          error: {
            code: "PAYLOAD_TOO_LARGE",
            message: "Request body is too large.",
          },
        },
      };
    }

    chunks.push(buffer);
  }

  try {
    return {
      ok: true,
      value: JSON.parse(Buffer.concat(chunks).toString("utf8") || "{}"),
    };
  } catch {
    return {
      ok: false,
      statusCode: 400,
      response: {
        error: {
          code: "MALFORMED_JSON",
          message: "Request body must be valid JSON.",
        },
      },
    };
  }
}

function writeJson(response: ServerResponse, statusCode: number, body: unknown): void {
  response.writeHead(statusCode, {
    "content-type": "application/json; charset=utf-8",
  });
  response.end(JSON.stringify(body));
}
