import { useState } from "react"
import css from "./Toggle.module.css"

export function Toggle() {
    const [enabled, setEnabled] = useState<boolean>(false)

    return <div onClick={() => setEnabled(e => !e)} className={css.container}>
        <div className={css.slider + " " + (enabled ? css.slider_enabled : "")} />
        <div className={css.thumb + " " + (enabled ? css.thumb_enabled : "")} />
    </div>
}