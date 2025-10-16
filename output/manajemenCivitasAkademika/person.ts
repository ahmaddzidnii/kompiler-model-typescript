import { IPerson } from './iperson';

/**
 * Superclass untuk semua individu dalam sistem.
 */
export abstract class Person implements IPerson {
  public id: string;
  public nama?: string;
  public email?: string;

  constructor(id: string) {
    this.id = id;
  }
}
