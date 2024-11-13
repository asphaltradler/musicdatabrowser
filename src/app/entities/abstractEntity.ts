export abstract class AbstractEntity {
  id!: number;
  name!: string;

  static entityName: string;
  static namePlural: string;

  /** EntityName aber vorne großgeschrieben */
  static getNameSingular() {
    return this.entityName.replace(/\b(\w)/g, s => s.toUpperCase());
  }
}
