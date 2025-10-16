import { IMatakuliah } from './imatakuliah';

/**
 * Kelas pasif yang merepresentasikan mata kuliah.
 */
export class Matakuliah implements IMatakuliah {
  public kodemk: string;
  public namamk?: string;
  public sks?: number;

  constructor(kodemk: string) {
    this.kodemk = kodemk;
  }
}
