import { createTheme } from "@mui/material/styles";
export const DarkTheme = createTheme({
  palette: {
    mode: "dark",
    primary: {
      main: "#e91e63",
    },
    secondary: {
      main: "#5A20CD",
    },
    black: {
      main: "#242B2E",
    },
    background: {
      default: "#0D0D0D",
      paper: "#0D0D0D",
    },
    text: {
      primary: "#ffffff",
      secondary: "#B0BEC5",
    },
  },
});
