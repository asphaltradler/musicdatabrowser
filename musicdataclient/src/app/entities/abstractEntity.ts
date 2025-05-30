/**
 * Abstract base class for entities with common properties and utility methods.
 *
 * @remarks
 * This class provides a template for entities that have an `id`, `name`, and optional album art properties.
 * It also includes static methods for formatting entity names and descriptions.
 *
 * @property id - Unique identifier for the entity.
 * @property name - Name of the entity.
 * @property albumartId - Optional identifier for the album art.
 * @property albumartName - Optional name for the album art.
 *
 * @static entityName - Singular name of the entity type (should be set in derived classes).
 * @static namePlural - Plural name of the entity type (should be set in derived classes).
 *
 * @method static getNameSingular - Returns the entity name with the first letter capitalized.
 * @method static getUppercaseFirst - Capitalizes the first letter of each word in a string.
 * @method static getNumberDescription - Returns a string describing the number of entities, using singular or plural form as appropriate.
 * @method static getNumbersDescription - Returns a string describing a range of entities, using plural form.
 */
export abstract class AbstractEntity {
  id!: number;
  name!: string;
  albumartId?: Number;
  albumartName?: string;

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

  static getNumbersDescription(from: number, to: number) {
    if (from === to) {
      return '1 ' + this.getNameSingular();
    }
    return `${this.namePlural} ${from}-${to}`;
  }
}
