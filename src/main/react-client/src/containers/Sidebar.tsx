/* eslint-disable react/prop-types */
import React, { FC } from 'react'
import {
    ProSidebar,
    Menu,
    SidebarHeader,
    SidebarContent,
    SidebarFooter,
} from 'react-pro-sidebar'
import { useAppSelector } from '../redux/hooks'
import ClassificationList from '../components/sidebar/ClassificationList'
import '../components/custom.scss'

const Sidebar: FC = () => {
    const classifications = useAppSelector(
        (state) => state.classification.entities
    )
    /**
     * TODO set the onToggle value for subMenu change to clear selected subMenu state
     */
    return (
        <div className="sidebar">
            <div className="sidebar-content">
                <ProSidebar width="16rem">
                    <SidebarHeader>
                        <p className="sidebar-header">Classifications</p>
                    </SidebarHeader>
                    <SidebarContent>
                        <Menu iconShape="square">
                            {classifications.length !== 0 && (
                                <ClassificationList
                                    classifications={classifications}
                                />
                            )}
                        </Menu>
                    </SidebarContent>
                    <SidebarFooter>
                        <div className="sidebar-footer">
                            <footer>
                                <small>v0.5 {new Date().getFullYear()}</small>
                            </footer>
                        </div>
                    </SidebarFooter>
                </ProSidebar>
            </div>
        </div>
    )
}

export default Sidebar
