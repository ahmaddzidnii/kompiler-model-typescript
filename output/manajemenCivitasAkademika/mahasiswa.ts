import { IMahasiswa } from './imahasiswa';
import { Person } from './person';

/**
 * Merepresentasikan seorang mahasiswa yang terdaftar.
 */
export class Mahasiswa extends Person implements IMahasiswa {
  public nim?: string;
  public status?: string = 'Aktif';
  public state: 'Aktif' | 'Cuti' | 'Lulus' = 'Aktif';

  constructor(id: string) {
    super(id);
  }

  // State Machine Methods
  public ambilcuti(): boolean {
    if (this.state === 'Aktif') {
      this.state = 'Cuti';
      console.log(`Mahasiswa `+ this.nim +` mengajukan cuti.`);
      return true;
    }
    return false;
  }

  public aktifkankembali(): boolean {
    if (this.state === 'Cuti') {
      this.state = 'Aktif';
      console.log(`Mahasiswa `+ this.nim +` aktif kembali.`);
      return true;
    }
    return false;
  }

  public dinyatakanlulus(): boolean {
    if (this.state === 'Aktif') {
      this.state = 'Lulus';
      console.log(`Mahasiswa `+ this.nim +` telah lulus.`);
      return true;
    }
    return false;
  }

}
