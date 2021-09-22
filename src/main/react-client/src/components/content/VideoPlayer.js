/* eslint-disable react/prop-types */
import React from 'react';
import ReactPlayer from 'react-player';

const VideoPlayer = ({ selectedVideo }) => (
  <ReactPlayer
    className="react-player"
    url={selectedVideo.link}
    volume={null}
    muted
    playing
    loop
    width="85%"
    height="85%"
    controls
  />
);
export default VideoPlayer;
