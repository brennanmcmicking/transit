import { useCallback, useId, useMemo, useRef, useState } from "react"
import { createPortal } from "react-dom"
import css from "./StopMarker.module.css"
import { useClickAway } from "../../hooks/useClickAway"
import StopIcon from "../icons/StopIcon"
import type { Departure, Stop } from "../../data"
import { useControlPane } from "../../providers/ControlPaneProvider"
import { useClient } from "../../util/client"


interface StopMarkerProps {
    latitude: number
    longitude: number
    stop: Stop
}

export function StopMarker(props: StopMarkerProps) {
    const id = useId()
    const ref = useRef(null)
    const client = useClient()
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { viewStateHook } = useControlPane()
    const setControlPane = viewStateHook[1]
    const icon = useMemo(() => divIcon({
        html: `<div id="${id}"></div>`,
        className: '',
        iconSize: [8, 8],
        iconAnchor: [4, 4],
    }), [id])

    const [markerRendered, setMarkerRendered] = useState(false)
    const [state, setState] = useState<"none" | "loading" | "failed" | "focused">("none")

    const handleClickAway = useCallback(() => setState('none'), [])
    useClickAway(id, handleClickAway)
    const target = document.getElementById(id)
    const contentRef = useRef(null)
    const content =
        <div
            className={state === 'loading' ? css.stop_marker_focused : css.stop_marker}
            ref={contentRef}
        >
            <StopIcon />
        </div>

    const showStopInfo = () => {
        setState('loading')
        client.getDeparturesForStop(props.stop.id)
            .then((departures: Departure[]) => setControlPane({
                type: 'stop',
                stop: props.stop,
                departures
            }))
            .catch(() => setState("failed"))
    }


    return <>
        {markerRendered && target && createPortal(
            content,
            target
        )}
        <Marker
            position={props.position}
            icon={icon}
            ref={ref}
            eventHandlers={{
                add: () => setMarkerRendered(true),
                remove: () => setMarkerRendered(false),
                click: showStopInfo
            }}
        />
    </>
}
