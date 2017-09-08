import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { PositionComponent } from './position.component';
import { PositionDetailComponent } from './position-detail.component';
import { PositionPopupComponent } from './position-dialog.component';
import { PositionDeletePopupComponent } from './position-delete-dialog.component';

export const positionRoute: Routes = [
    {
        path: 'position',
        component: PositionComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Positions'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'position/:id',
        component: PositionDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Positions'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const positionPopupRoute: Routes = [
    {
        path: 'position-new',
        component: PositionPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Positions'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'position/:id/edit',
        component: PositionPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Positions'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'position/:id/delete',
        component: PositionDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Positions'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
