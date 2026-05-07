import type { Property } from "csstype";
import type { MouseEventHandler, ReactNode } from "react";

interface FloatingButtonProps {
    top: Property.Top
    left: Property.Left
    right: Property.Right
    onClick: MouseEventHandler<HTMLDivElement>
    children: ReactNode
}

export function FloatingButton(props: FloatingButtonProps) {

    return <div style={{
        position: 'absolute',
        top: props.top,
        left: props.left,
        right: props.right,
        width: '32px',
        height: '32px',
        zIndex: '999',
        background: '#006715',
        borderRadius: '6px',
        boxShadow: '0 0 4px black',
    }}
        onClick={props.onClick}
    >
        {props.children}
    </div>
}