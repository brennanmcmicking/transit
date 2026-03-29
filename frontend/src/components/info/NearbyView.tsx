import { useEffect, useState } from "react";
import type { DepartureAndDistance } from "../../data";
import { useClient } from "../../util/client";
import { DepartureCard } from "./DepartureCard";
import { Ellipsis } from "lucide-react";
import { Flex } from "../containers/Flex";

interface NearbyViewProps {
    location: GeolocationPosition
}

export function NearbyView(props: NearbyViewProps) {
    const [nearby, setNearby] = useState<DepartureAndDistance[]>()
    const client = useClient()

    useEffect(() => {
        client
            .getNearbyDepartures(
                props.location.coords.latitude,
                props.location.coords.longitude,
                2
            )
            .then(setNearby)
    }, [props.location]);

    return <div style={{
        display: 'flex',
        flexDirection: 'column',
        width: '100%',
        pointerEvents: 'all',
        overflow: 'scroll',
        height: '100%',
    }}>
        {nearby?.map(departure => <DepartureCard departure={departure.departure} distanceKm={departure.distance} />)}
        {!!!nearby && <Flex align="center" justify="center"><Ellipsis /></Flex>}
    </div>
}