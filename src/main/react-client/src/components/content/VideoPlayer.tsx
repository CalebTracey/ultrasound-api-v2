/* eslint-disable react/prop-types */
import React, { FC } from 'react'
import ReactPlayer from 'react-player'
import SyncLoader from 'react-spinners/SyncLoader'
import { useAppSelector } from '../../redux/hooks'
import DetailsPopover from '../DetailsPopover'
import { IListItem } from '../../schemas'

const VideoPlayer: FC = () => {
    const { selected, editing } = useAppSelector((state) => state.item)
    const { loading, url } = useAppSelector((state) => state.item)

    const isUrl = (value: unknown): value is string => {
        return !!value && !!(value as string)
    }
    const isItemList = (value: unknown): value is IListItem => {
        return !!value && !!(value as IListItem)
    }
    return isUrl(url) ? (
        <div className="video-page">
            <div className="video-page___header">
                {isItemList(selected) && <DetailsPopover item={selected} />}
                <h2 className="video-page___title">
                    {!editing && selected.title}
                </h2>
            </div>

            <div className="player">
                <ReactPlayer
                    className="react-player"
                    url={url}
                    volume={0}
                    muted
                    playing
                    loop
                    width="85%"
                    height="85%"
                    controls
                />
            </div>
        </div>
    ) : (
        <div className="spinner">
            <SyncLoader />
        </div>
    )
}
export default VideoPlayer
