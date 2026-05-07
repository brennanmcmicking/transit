import { SquareArrowLeftIcon } from "lucide-react"
import { useControlPane } from "../providers/ControlPaneProvider"
import { FloatingButton } from "./FloatingButton"

interface BackButtonProps {
    location: GeolocationPosition
}

export function BackButton(props: BackButtonProps) {
    const { viewStateHook } = useControlPane()
    const setControlPane = viewStateHook[1]
    return <FloatingButton
        top='-40px'
        left='8px'
        right="auto"
        onClick={() => setControlPane({
            type: 'nearby-departures',
            latitude: props.location.coords.latitude,
            longitude: props.location.coords.longitude,
        })}
    >
        <SquareArrowLeftIcon width="32px" height="32px" />
    </FloatingButton>
}