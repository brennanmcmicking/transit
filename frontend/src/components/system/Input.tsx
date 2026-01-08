import css from "./Input.module.css";

export function Input() {
  return (
    <input
      type="text"
      placeholder="Type something..."
      className={css.input}
    />
  )
}