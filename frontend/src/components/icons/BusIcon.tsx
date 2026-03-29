import * as React from "react";

const BusIcon = (props: React.HTMLAttributes<HTMLDivElement>) => (
    <div {...props}>
        <svg
            xmlns="http://www.w3.org/2000/svg"
            id="svg2"
            version="1.1"
            viewBox="0 0 14 14"
        >
            <path
                id="bus-stop"
                fill="#000"
                fillOpacity="1"
                stroke="none"
                d="M3 0C2 0 1 1 1 2v10.484h1C2 13 2 14 3 14s1-1 1-1.516l6 .032C10 13 10 14 11 14s1-1 1-1.484h1V2c0-1-1-2-2-2zm1 1h6v1H4zM3 2.969 11 3v4l-8-.031zM4 9a1 1 0 1 1 0 2 1 1 0 0 1 0-2m6 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2"
            ></path>
        </svg>
    </div>
);

export default BusIcon;
