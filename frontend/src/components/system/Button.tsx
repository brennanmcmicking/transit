import { useId, useMemo, type ReactNode } from "react";
import css from "./Button.module.css";
import Leaf1 from "./icons/Leaf1";
import { generateHash } from "../../util/hash";

interface ButtonProps {
    children: ReactNode
    onClick: () => void
}

export function Button(props: ButtonProps) {
    const id = useId()
    const seed = useMemo(() => {
        return generateHash(id) % 360
    }, [id])

    return (
        <button className={css.button} onClick={props.onClick} style={{ position: "relative" }}>
            {props.children}
            {/* <Leaf1 style={{
                width: "32px",
                height: "32px",
                position: "absolute",
                left: -16,
                top: 0,
                transform: `rotate(${seed % 360}deg)`
            }} /> */}
        </button>
    );
}