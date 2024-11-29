export abstract class AbstractEntity {
  id!: number;
  name!: string;

  static entityName: string;
  static namePlural: string;

  /** EntityName aber vorne großgeschrieben */
  static getNameSingular() {
    return AbstractEntity.getUppercaseFirst(this.entityName);
  }

  static getUppercaseFirst(s: string) {
    return s.replace(/\b(\w)/g, s => s.toUpperCase());
  }

  static getNumberDescription(num?: number) {
    return `${num} ${num === 1
      ? this.getNameSingular()
      : this.namePlural}`;
  }
}
