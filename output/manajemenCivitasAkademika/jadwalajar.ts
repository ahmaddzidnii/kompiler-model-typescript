import { IJadwalajar } from './ijadwalajar';

/**
 * Association class for relationship R2
 */
export class Jadwalajar implements IJadwalajar {
  public jadwalId: string;
  public dosenId?: string;
  public matakuliahKodemk?: string;
  public tahunakademik?: string;

  constructor(jadwalId: string) {
    this.jadwalId = jadwalId;
  }
}
