import { Map as LibreMap, type MapRef } from "react-map-gl/maplibre";
import type { Bus, Stop } from "../../data";
import 'maplibre-gl/dist/maplibre-gl.css';
import { useRef } from "react";
import { BusMarker } from "./BusMarker";
import { UserMarker } from "./UserMarker";
import { useClient } from "../../util/client";
import { useControlPane } from "../../providers/ControlPaneProvider";

interface MapProps {
    location: GeolocationPosition
    busses: Bus[]
    stops: Stop[]
}

export function Map(props: MapProps) {
    // const pos = [props.location.coords.latitude, props.location.coords.longitude] as LatLngExpression;

    const mapRef = useRef<MapRef>(null)
    const client = useClient()

    const { viewStateHook } = useControlPane()

    const pos = props.location.coords
    const latitude = pos.latitude
    const longitude = pos.longitude

    return <LibreMap
        // attributionControl={{ compact: false }}
        ref={mapRef}
        interactiveLayerIds={['poi_transit']}
        initialViewState={{
            latitude: props.location.coords.latitude,
            longitude: props.location.coords.longitude,
            zoom: 14
        }}
        style={{ width: '100%', height: '60%' }}
        mapStyle="https://tiles.openfreemap.org/styles/liberty"
        onClick={(e) => {
            // const layers = mapRef.current?.getStyle().layers
            // const features = mapRef.current?.queryRenderedFeatures(e.point, {
            //     layers: ["poi_transit"]
            // })
            // console.log(layers)
            // console.log(layers?.map(l => l.id).filter(l => l.includes('transit')))
            // console.log(features)
            // console.log(e.features)
            if (e.features && e.features.length > 0 && e.features[0].geometry.type === 'Point') {
                // console.log(`clicked on bus stop data=${JSON.stringify(e.features[0].geometry.coordinates)}`)
                const coords = e.features[0].geometry.coordinates
                const longitude = coords[0]
                const latitude = coords[1]
                console.log(`latitude=${latitude} longitude=${longitude}`)
                client.getDeparturesForStopByPosition(latitude, longitude).then(r => {
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                    const [_, setViewState] = viewStateHook
                    setViewState({
                        type: 'stop',
                        stop: {
                            id: "1234",
                            name: 'asdf',
                            latitude: latitude,
                            longitude: longitude,
                            wheelchair: false,
                            code: "asdf"
                        },
                        departures: r
                    })
                })
            }
        }}
    >
        {props.busses.filter(bus => bus.latitude != undefined && bus.longitude !== undefined).map(bus =>
            <BusMarker latitude={bus.latitude} longitude={bus.longitude} route={bus.label} key={bus.id} />
        )}
        <UserMarker latitude={latitude} longitude={longitude} />
    </LibreMap>

    // return <MapContainer
    //     center={pos}
    //     zoom={15}
    //     scrollWheelZoom={true}
    //     touchZoom={true}
    //     style={{ width: '100%', height: '60%' }}
    //     attributionControl={false}
    // >
    //     <TileLayer
    //         attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>'
    //         // url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
    //         url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
    //         subdomains="abcd"
    //     />
    //     <AttributionControl position="topright" />
    //     {/* <GeoJSON data={} style={ } /> */}
    //     <UserMarker position={props.location} />
    //     {props.busses.filter(bus => bus.latitude != undefined && bus.longitude !== undefined).map(bus =>
    //         <BusMarker position={[bus.latitude, bus.longitude]} route={bus.label} key={bus.id} />
    //     )}
    //     {props.stops.map(stop =>
    //         <StopMarker position={[stop.latitude, stop.longitude]} stop={stop} key={stop.id} />
    //     )}
    // </MapContainer>
}