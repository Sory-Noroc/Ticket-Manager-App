import { createTheme } from '@mui/material/styles';

// Create a theme instance.
const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#90caf9', // A lighter blue for primary elements in dark mode
    },
    secondary: {
      main: '#f48fb1', // A pinkish color for secondary elements
    },
    background: {
      default: '#121212', // Standard dark background
      paper: '#1e1e1e',   // Slightly lighter for surfaces like cards and menus
    },
    text: {
      primary: '#ffffff',
      secondary: '#b0bec5',
    },
  },
  typography: {
    h5: {
      fontWeight: 500,
    },
  },
});

export default theme;
