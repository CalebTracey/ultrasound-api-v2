/* eslint-disable react/jsx-props-no-spreading */
/* eslint-disable react/destructuring-assignment */
/* eslint-disable react/prop-types */
import React, { useEffect, useState, FC, useCallback } from 'react'
import { withRouter, Redirect, RouteComponentProps } from 'react-router-dom'
import { Media, Jumbotron, Container, Alert } from 'reactstrap'
import { useAppSelector, useAppDispatch } from '../redux/hooks'
import EditSubMenuContainer from '../components/edit/EditSubMenuContainer'
import EditItemListContainer from '../components/edit/EditItemListContainer'
import EditDataName from '../components/edit/EditDataName'
import allActions from '../redux/actions'
import DeleteButton from '../components/buttons/DeleteButton'

interface Props {
    history: RouteComponentProps['history']
}

const Edit: FC<Props> = ({ history }) => {
    const { roles } = useAppSelector((state) => state.auth.user)
    const { message } = useAppSelector((state) => state.message)
    const { selectedEdit } = useAppSelector((state) => state.data)
    const { _id, name, subMenus, listItems, hasSubMenu } = selectedEdit

    const [editingSubMenu, setEditingSubMenu] = useState(false)
    const [editingListItem, setEditingListItem] = useState(false)
    const dispatch = useAppDispatch()

    const handleCancel = useCallback(() => {
        dispatch(allActions.data.clearSelectedSubMenu())
        setEditingSubMenu(false)
        setEditingListItem(false)
    }, [dispatch])

    useEffect(() => {
        history.listen((location) => {
            handleCancel()
        })
    }, [dispatch, history, handleCancel])

    return roles.includes('ROLE_ADMIN') && selectedEdit.name !== undefined ? (
        <Jumbotron>
            <div className="edit-content">
                <Container>
                    {message !== '' && <Alert color="info">{message}</Alert>}
                    <Media body>
                        <h4 className="lead">Editing:</h4>
                        <Media heading>
                            <div style={{ display: 'flex' }}>
                                <span className="display-4">
                                    {selectedEdit.name.toUpperCase()}
                                </span>
                                <EditDataName
                                    id={_id}
                                    currentName={name}
                                    type="classification"
                                />
                                <DeleteButton
                                    id={_id}
                                    type="classification"
                                    title="Delete"
                                />
                                {/* <ResetButton reset={reset} /> */}
                            </div>
                            <hr className="my-2" />
                        </Media>
                        <Container
                            fluid
                            style={{ display: 'flex', padding: '2rem' }}
                        >
                            {!editingListItem && (
                                <EditSubMenuContainer
                                    handleCancel={handleCancel}
                                    editingSubMenu={editingSubMenu}
                                    setEditingSubMenu={setEditingSubMenu}
                                    hasSubMenu={hasSubMenu}
                                    subMenus={subMenus}
                                    classificationId={_id}
                                />
                            )}
                            {!editingSubMenu && (
                                <EditItemListContainer
                                    handleCancel={handleCancel}
                                    classificationId={_id}
                                    listItems={listItems}
                                    editingListItem={editingListItem}
                                    setEditingListItem={setEditingListItem}
                                />
                            )}
                        </Container>
                    </Media>
                </Container>
            </div>
        </Jumbotron>
    ) : (
        <Redirect
            to={{
                pathname: '/dashboard',
                state: history.location,
            }}
        />
    )
}

export default withRouter(Edit)