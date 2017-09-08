import { BaseEntity } from './../../shared';

const enum OperationType {
    'SHORT',
    'LONG'
}

export class Position implements BaseEntity {
    constructor(
        public id?: number,
        public asset?: string,
        public buyAt?: any,
        public sellAt?: any,
        public entryValue?: number,
        public exitValue?: number,
        public operationType?: OperationType,
    ) {
    }
}
