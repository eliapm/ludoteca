import { createContext, useState, type JSX } from "react";
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";

export const LoaderContext = createContext({
  loading: false,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  showLoading: (_show: boolean) => {},
});

type Props = {
  children: JSX.Element;
};

export const LoaderProvider = ({ children }: Props) => {
  const [loading, setLoading] = useState(false);

  const showLoading = (show: boolean) => {
    setLoading(show);
  };

  return (
    <LoaderContext.Provider value={{ loading, showLoading }}>
      <Backdrop
        sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={loading}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
      {children}
    </LoaderContext.Provider>
  );
};