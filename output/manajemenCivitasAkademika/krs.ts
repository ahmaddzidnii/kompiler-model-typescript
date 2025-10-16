import { IKrs } from './ikrs';

/**
 * Association class for relationship R1
 */
export class Krs implements IKrs {
  public krsId: string;
  public mahasiswaId?: string;
  public matakuliahKodemk?: string;
  public semester?: string;
  public nilai?: string;

  constructor(krsId: string) {
    this.krsId = krsId;
  }
}
