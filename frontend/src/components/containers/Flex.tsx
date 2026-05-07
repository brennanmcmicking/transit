import type { Property } from "csstype"
import type { ReactNode } from "react"


interface FlexProps {
    // basis?: Property.FlexBasis<any>
    direction?: Property.FlexDirection
    flow?: Property.FlexFlow
    grow?: Property.FlexGrow
    shrink?: Property.FlexShrink
    wrap?: Property.FlexWrap
    gap?: Property.Gap
    justify?: Property.JustifyContent
    align?: Property.AlignItems
    className?: string
    children: ReactNode
}

export function Flex(props: FlexProps) {
    return <div
        className={props.className}
        style={{
            display: 'flex',
            flexDirection: props.direction,
            justifyContent: props.justify,
            alignItems: props.align,
            gap: props.gap,
            // flexBasis: props.basis,
            // flexDirection: props.direction,
            // flexFlow: props.flow,
            // flexGrow: props.grow,
            // flexShrink: props.shrink,
            // flexWrap: props.wrap,
        }}
    >
        {props.children}
    </div>
}