import { divIcon, type LatLngExpression } from "leaflet"
import { useCallback, useId, useMemo, useRef, useState, type ReactNode, type Ref } from "react"
import { createPortal } from "react-dom"
import { Marker } from "react-leaflet"
import css from "./StopMarker.module.css"
import { useClickAway } from "../../hooks/useClickAway"
import StopIcon from "../icons/StopIcon"
import { Box } from "../system/Box"
import type { Departure, Stop } from "../../data"
import { useControlPane } from "../../providers/ControlPaneProvider"


interface StopMarkerProps {
    position: LatLngExpression
    stop: Stop
}

export function StopMarker(props: StopMarkerProps) {
    const id = useId()
    const ref = useRef(null)
    const [controlPane, setControlPane] = useControlPane()
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
        fetch(`http://localhost:8080/v1/departures-for-stop?stopId=${props.stop.id}`)
            .then(res => res.json())
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

interface PopoverProps {
    open: boolean
    anchorElement: Ref<HTMLElement>
    children: ReactNode
}

function Popover(props: PopoverProps) {
    return <>
        <div style={{
            position: 'relative',
            display: 'flex',
            justifyContent: 'center',
            width: '200px'
        }}>
            {props.open && <Box
                className={props.open ? css.popover : css.popover_hidden}
            >
                {props.children}
            </Box>}
        </div>
    </>
}