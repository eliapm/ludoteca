import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Game } from "./pages/Game/Game";
import { Author } from "./pages/Author/Author";
import { Category } from "./pages/Category/Category";
import { Client } from "./pages/Client/Client";
import { Loan } from "./pages/Loan/Loan";
import { Layout } from "./components/Layout";
import { Provider } from "react-redux";
import { LoaderProvider } from "./context/LoaderProvider";
import { store } from "./redux/store";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";


function App() {
  return (
    <LoaderProvider>
      <Provider store={store}>
        <LocalizationProvider dateAdapter={AdapterDateFns}>
        <BrowserRouter>
          <Routes>
            <Route element={<Layout />}>
              <Route index element={<Navigate to="games" />} />
              <Route path="games" element={<Game />} />
              <Route path="categories" element={<Category />} />
              <Route path="authors" element={<Author />} />
              <Route path="clients" element={<Client />} />
              <Route path="loans" element={<Loan />} />
              <Route path="*" element={<Navigate to="games" />} />
            </Route>
          </Routes>
        </BrowserRouter>
        </LocalizationProvider>
      </Provider>
    </LoaderProvider>
  );
}

export default App;
