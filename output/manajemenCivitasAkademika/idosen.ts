import { IPerson } from './iperson';

/**
 * Merepresentasikan seorang dosen pengajar.
 */
export interface IDosen extends IPerson {
  nip?: string;
  state: 'Aktif Mengajar' | 'Cuti Akademik' | 'Pensiun';
}
