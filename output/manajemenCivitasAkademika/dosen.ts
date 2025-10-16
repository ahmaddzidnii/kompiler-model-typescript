import { IDosen } from './idosen';
import { Person } from './person';

/**
 * Merepresentasikan seorang dosen pengajar.
 */
export class Dosen extends Person implements IDosen {
  public nip?: string;
  public state: 'Aktif Mengajar' | 'Cuti Akademik' | 'Pensiun' = 'Aktif Mengajar';

  constructor(id: string) {
    super(id);
  }

  // State Machine Methods
  public ajukancuti(): boolean {
    if (this.state === 'Aktif Mengajar') {
      this.state = 'Cuti Akademik';
      return true;
    }
    return false;
  }

  public selesaicuti(): boolean {
    if (this.state === 'Cuti Akademik') {
      this.state = 'Aktif Mengajar';
      return true;
    }
    return false;
  }

  public memasukipensiun(): boolean {
    if (this.state === 'Aktif Mengajar') {
      this.state = 'Pensiun';
      return true;
    }
    return false;
  }

}
