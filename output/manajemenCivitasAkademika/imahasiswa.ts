import { IPerson } from './iperson';

/**
 * Merepresentasikan seorang mahasiswa yang terdaftar.
 */
export interface IMahasiswa extends IPerson {
  nim?: string;
  status?: string;
  state: 'Aktif' | 'Cuti' | 'Lulus';
}
