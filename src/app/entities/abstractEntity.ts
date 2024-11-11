export abstract class AbstractEntity {
  id!: number;
  name!: string;

  static entityName: string;
  static namePlural: string;
}
