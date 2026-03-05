/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#4361ee",
        success: "#2ec4b6",
        danger: "#e63946",
        warning: "#ff9f1c",
        "light-bg": "#f5f7fa",
        "panel-light": "#ffffff",
        "text-charcoal": "#333333",
        "border-gray": "#e2e8f0"
      },
      fontFamily: {
        display: ['Inter', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      borderRadius: {
        none: '0px',
        sm: '0px',
        DEFAULT: '0px',
        md: '0px',
        lg: '0px',
        xl: '0px',
        '2xl': '0px',
        '3xl': '0px',
        full: '0px',
      }
    },
  },
  plugins: [],
};

