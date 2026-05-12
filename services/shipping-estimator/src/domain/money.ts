const MONEY_SCALE = 2;

export class Decimal {
  private constructor(
    private readonly units: bigint,
    private readonly scale: number,
  ) {}

  static zero(): Decimal {
    return new Decimal(0n, 0);
  }

  static parse(value: string): Decimal {
    if (!isDecimalString(value)) {
      throw new Error(`Invalid decimal: ${value}`);
    }

    const [wholePart, fractionalPart = ""] = value.split(".");
    const normalizedWhole = wholePart === "" ? "0" : wholePart;
    const scale = fractionalPart.length;
    const magnitude = BigInt(`${normalizedWhole}${fractionalPart}` || "0");

    return new Decimal(magnitude, scale).normalize();
  }

  add(other: Decimal): Decimal {
    const scale = Math.max(this.scale, other.scale);
    return new Decimal(this.align(scale) + other.align(scale), scale).normalize();
  }

  multiply(other: Decimal): Decimal {
    return new Decimal(this.units * other.units, this.scale + other.scale).normalize();
  }

  compare(other: Decimal): number {
    const scale = Math.max(this.scale, other.scale);
    const left = this.align(scale);
    const right = other.align(scale);

    if (left === right) {
      return 0;
    }

    return left > right ? 1 : -1;
  }

  lessThanOrEqual(other: Decimal): boolean {
    return this.compare(other) <= 0;
  }

  toMoneyString(): string {
    const cents = roundHalfEven(this.units, this.scale, MONEY_SCALE);
    const whole = cents / 100n;
    const fractional = (cents % 100n).toString().padStart(2, "0");

    return `${whole}.${fractional}`;
  }

  toDecimalString(): string {
    const moneyString = this.toMoneyString();

    return moneyString;
  }

  private align(targetScale: number): bigint {
    return this.units * 10n ** BigInt(targetScale - this.scale);
  }

  private normalize(): Decimal {
    let normalizedUnits = this.units;
    let normalizedScale = this.scale;

    while (normalizedScale > 0 && normalizedUnits % 10n === 0n) {
      normalizedUnits /= 10n;
      normalizedScale -= 1;
    }

    return new Decimal(normalizedUnits, normalizedScale);
  }
}

export function isDecimalString(value: unknown): value is string {
  return typeof value === "string" && /^(?:0|[1-9]\d*)(?:\.\d+)?$|^\.\d+$/.test(value);
}

function roundHalfEven(units: bigint, currentScale: number, targetScale: number): bigint {
  if (currentScale <= targetScale) {
    return units * 10n ** BigInt(targetScale - currentScale);
  }

  const divisor = 10n ** BigInt(currentScale - targetScale);
  const quotient = units / divisor;
  const remainder = units % divisor;
  const halfway = divisor / 2n;

  if (remainder < halfway) {
    return quotient;
  }

  if (remainder > halfway) {
    return quotient + 1n;
  }

  return quotient % 2n === 0n ? quotient : quotient + 1n;
}
