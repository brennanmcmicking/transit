import type { CSSProperties, ReactNode } from "react";
import css from "./Box.module.css";

interface BoxProps {
    children: ReactNode;
    style?: CSSProperties
}

export function Box({ children, style }: BoxProps) {
    return (
        <div className={css.box} style={style}>
            {children}
        </div>
    );
}