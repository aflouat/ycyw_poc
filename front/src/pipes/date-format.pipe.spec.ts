
import { DateFormatPipe } from './date-format.pipe';

describe('DateFormatPipe', () => {
  it('create an instance', () => {
    const pipe = new DateFormatPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return empty string for undefined', () => {
    const pipe = new DateFormatPipe();
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should return empty string for invalid date string', () => {
    const pipe = new DateFormatPipe();
    expect(pipe.transform('not-a-date')).toBe('');
  });

  it('should format valid date string to fr-FR long format', () => {
    const pipe = new DateFormatPipe();
    const result = pipe.transform('2025-09-30 10:20:30.000000');
    // Expect French long month/day/year presence
    expect(result).toContain('30');
    expect(result).toContain('2025');
  });

  it('should accept Date object input', () => {
    const pipe = new DateFormatPipe();
    const d = new Date('2024-12-25T00:00:00');
    const result = pipe.transform(d);
    expect(result).toContain('25');
    expect(result).toContain('2024');
  });

  it('should return empty string for invalid Date object', () => {
    const pipe = new DateFormatPipe();
    const d = new Date('invalid');
    const result = pipe.transform(d as any);
    expect(result).toBe('');
  });
});
