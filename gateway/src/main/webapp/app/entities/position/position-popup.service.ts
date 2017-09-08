import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Position } from './position.model';
import { PositionService } from './position.service';

@Injectable()
export class PositionPopupService {
    private ngbModalRef: NgbModalRef;

    constructor(
        private modalService: NgbModal,
        private router: Router,
        private positionService: PositionService

    ) {
        this.ngbModalRef = null;
    }

    open(component: Component, id?: number | any): Promise<NgbModalRef> {
        return new Promise<NgbModalRef>((resolve, reject) => {
            const isOpen = this.ngbModalRef !== null;
            if (isOpen) {
                resolve(this.ngbModalRef);
            }

            if (id) {
                this.positionService.find(id).subscribe((position) => {
                    if (position.buyAt) {
                        position.buyAt = {
                            year: position.buyAt.getFullYear(),
                            month: position.buyAt.getMonth() + 1,
                            day: position.buyAt.getDate()
                        };
                    }
                    if (position.sellAt) {
                        position.sellAt = {
                            year: position.sellAt.getFullYear(),
                            month: position.sellAt.getMonth() + 1,
                            day: position.sellAt.getDate()
                        };
                    }
                    this.ngbModalRef = this.positionModalRef(component, position);
                    resolve(this.ngbModalRef);
                });
            } else {
                // setTimeout used as a workaround for getting ExpressionChangedAfterItHasBeenCheckedError
                setTimeout(() => {
                    this.ngbModalRef = this.positionModalRef(component, new Position());
                    resolve(this.ngbModalRef);
                }, 0);
            }
        });
    }

    positionModalRef(component: Component, position: Position): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.position = position;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.ngbModalRef = null;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.ngbModalRef = null;
        });
        return modalRef;
    }
}
