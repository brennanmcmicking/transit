import type { ReactNode } from "react";
import css from "./Button.module.css";

interface ButtonProps {
    children: ReactNode
    onClick: () => void
}

export function Button(props: ButtonProps) {
    return (
        <button className={css.button} onClick={props.onClick}>
            {props.children}
        </button>
    );
}