import type { CSSProperties, ReactNode } from "react";
import css from "./Box.module.css";

interface BoxProps {
    children: ReactNode;
    style?: CSSProperties
    className?: string
}

export function Box(props: BoxProps) {
    return (
        <div className={css.box + (props.className !== undefined ? ` ${props.className}` : '')} style={props.style}>
            {props.children}
        </div>
    );
}