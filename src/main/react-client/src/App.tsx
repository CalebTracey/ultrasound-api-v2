import React, { FC, useEffect, useCallback, ChangeEvent } from 'react'
import { RouteComponentProps } from 'react-router-dom'
import { useAppSelector, useAppDispatch } from './redux/hooks'
// import history from './helpers/history'
import { CLEAR_MESSAGE } from './redux/actions/types'
import EventBus from './common/EventBus'
import allActions from './redux/actions'
import Routes from './routes/Routes'
import './Styles.css'

// interface IRouteProps extends RouteComponentProps {
//     history: RouteComponentProps['history']
// }

interface Props {
    history: RouteComponentProps['history']
}

const App: FC<Props> = ({ history }): JSX.Element => {
    const { user: currentUser } = useAppSelector((state) => state.auth)
    const dispatch = useAppDispatch()

    const logOut = useCallback(() => {
        dispatch(allActions.auth.logout())
    }, [dispatch])

    // onLocationUpdate = (location: Location<unknown>): void => {}

    useEffect(() => {
        history.listen((location) => {
            dispatch({ type: CLEAR_MESSAGE })
        })
    }, [dispatch, history])
    // useEffect(() => {
    // eslint-disable-next-line no-unused-vars
    // history.listen(location) => {
    // clear message when changing location
    // dispatch({ type: CLEAR_MESSAGE })
    // }
    // }, [dispatch, history])
    // useEffect(() => {
    //     window.addEventListener('popstate', (event) => {
    //         dispatch({ type: CLEAR_MESSAGE })
    //     })
    //     return () => window.removeEventListener('popstate')
    // }, [dispatch])

    useEffect(() => {
        EventBus.on('logout', () => {
            logOut()
        })
        return () => {
            EventBus.remove('logout')
        }
    }, [currentUser, dispatch, logOut])

    return (
        <div className="app">
            <Routes />
        </div>
    )
}
export default App
