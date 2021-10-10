/* eslint-disable react/prop-types */
import React, { FC, lazy, Suspense } from 'react'
import { Route, Router, Switch, Redirect, useHistory } from 'react-router-dom'
import SyncLoader from 'react-spinners/SyncLoader'
// import history from '../helpers/history'
import { useAppSelector } from '../redux/hooks'
import Edit from '../containers/Edit'
import { IAppUser } from '../schemas'
// import VideoPlayer from '../components/content/VideoPlayer'
// import ContentHome from '../components/content/ContentHome'
// import Classification from '../containers/Classification'
// import ProtectedRouteAdmin from './ProtectedRouteAdmin'

// const Edit = lazy(() => import('../containers/Edit'))
const VideoPlayer = lazy(() => import('../components/content/VideoPlayer'))
const Classification = lazy(() => import('../containers/Classification'))
const ContentHome = lazy(() => import('../components/content/ContentHome'))
const ProtectedRouteAdmin = lazy(() => import('./ProtectedRouteAdmin'))

interface Props {
    routePath: string
}

const ContentRoutes: FC<Props> = ({ routePath }) => {
    const { isAuth, user } = useAppSelector((state) => state.auth)
    const { loading } = useAppSelector((state) => state.classification)

    const isUser = (value: unknown): value is IAppUser => {
        return !!value && !!(value as IAppUser)
    }
    const history = useHistory()
    const isAdmin = isUser(user) && user.roles?.includes('ROLE_ADMIN')

    // console.log(isAdmin)

    return (
        // <Suspense
        //     fallback={
        //         <div className=`spinner`>
        //             <SyncLoader />
        //         </div>
        //     }
        // >
        // <Router history={history}>
        <Switch>
            <Route path={`${routePath}/home`} exact component={ContentHome} />
            <Route
                path={`${routePath}/classification/:id`}
                component={Classification}
            />
            {/* <ProtectedRouteAdmin
                    isAuthenticated={isAdmin}
                    path={`${routePath}/edit/:id`}
                    authenticationPath={`${routePath}`}
                    component={Edit}
                /> */}
            <Route path="/dashboard/admin/edit/:id" component={Edit} />
            {/* <ProtectedRoute
                        isAuthenticated={isAuth}
                        path=`${routePath}`
                        authenticationPath=`/login`
                        component={Dashboard}
                    /> */}
            <Route path={`${routePath}/video/:id`} component={VideoPlayer} />
            <Redirect from={`${routePath}`} to={`${routePath}/home`} exact />
        </Switch>
        // </Router>
        // </Suspense>
    )
}

export default ContentRoutes