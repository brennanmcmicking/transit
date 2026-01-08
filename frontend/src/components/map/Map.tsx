import type { LatLngExpression } from "leaflet";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import type { Bus } from "../../data";
import { BusMarker } from "./BusMarker";

interface MapProps {
    location: GeolocationPosition
    busses: Bus[]
}

export function Map(props: MapProps) {
    const pos = [props.location.coords.latitude, props.location.coords.longitude] as LatLngExpression;

    return <MapContainer
        center={pos}
        zoom={15}
        scrollWheelZoom={false}
        style={{ width: '100%', height: '100%' }}
    >
        <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://server.arcgisonline.com/ArcGIS/rest/services/NatGeo_World_Map/MapServer/tile/{z}/{y}/{x}"
        />
        <Marker position={pos} />
        {props.busses.filter(bus => bus.latitude != undefined && bus.longitude !== undefined).map(bus =>
            <BusMarker position={[bus.latitude, bus.longitude]} route={bus.label} />
        )}
    </MapContainer>
}