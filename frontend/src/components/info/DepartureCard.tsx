import { ArrowLeft, ArrowRight, SquareArrowLeft, SquareArrowRight } from "lucide-react"
import type { Departure, DepartureAndDistance } from "../../data"
import { useControlPane } from "../../providers/ControlPaneProvider"
import { minutesFromEpoch } from "../../util/time"
import { Flex } from "../containers/Flex"
import { Button } from "../system/Button"
import css from "./DepartureCard.module.css"

interface DepartureCardProps {
    departure: Departure
    distanceKm?: number
}

export function DepartureCard(props: DepartureCardProps) {
    const [controlPane, setControlPane] = useControlPane()

    const departure = props.departure
    const distance = props.distanceKm
    console.log(`departure.direction=${departure.direction}, departure.routeId=${departure.route.id}`)

    const minutes = minutesFromEpoch(departure.departureTime)
    return <div
        className={css.card_div}
        onClick={() => setControlPane({
            type: 'departure',
            departure
        })}
        style={{
            background: `linear-gradient(145deg, #${departure.route.colorHex}, lch(from #${departure.route.colorHex} calc(l - 10) c h))`,
            color: `#${departure.route.textColorHex}`,
        }}
        key={`${departure.route.id}:${departure.stop.id}:${departure.departureTime}`}
    >
        <Flex className={css.card_container}>
            <Flex className={css.route_column} justify="center" align="center">
                <div className={css.route_number}>{departure.route.id.split('-')[0]}</div>
            </Flex>
            <Flex direction="column" className={css.info_column} justify="center">
                <Flex gap="2px">
                    {/* <div style={{ aspectRatio: '1/1', height: '24px' }}>
                        {departure.direction === 0 ? <SquareArrowRight /> : <SquareArrowLeft />}
                    </div> */}
                    <div style={{ fontSize: '15px', textWrap: 'nowrap' }}>{departure.busHeader}</div>
                </Flex>
                <Flex justify="space-between">
                    <div style={{ fontSize: '10px' }}>{departure.stop.name}</div>
                </Flex>
                {distance && <Flex>
                    <div style={{ fontSize: '10px' }}>
                        {distance < 1 ? <>{distance * 1000} meters away</> : <>{distance} km away</>}
                    </div>
                </Flex>}
            </Flex>
            <Flex className={css.time_column} direction="column" justify="center" align="center">
                <div style={{ fontSize: '24px' }}>{minutes}</div>
                <div style={{ fontSize: '12px' }}>{minutes === 1 ? 'minute' : 'minutes'}</div>
            </Flex>
        </Flex>


    </div >
}