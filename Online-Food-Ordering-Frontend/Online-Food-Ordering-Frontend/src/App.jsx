import { ThemeProvider } from '@mui/material/styles';
import { Navbar } from './Component/Navbar/Navbar';
import './index.css'
import { CssBaseline } from '@mui/material'
import { DarkTheme } from './Theme/DarkTheme';
import Home from './Component/Home/home';

function App() {

  return (

    <ThemeProvider theme={DarkTheme}>
      <CssBaseline />
      <Navbar />
      <Home />

    </ThemeProvider>

  );
}

export default App