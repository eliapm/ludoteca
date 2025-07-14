import { useContext, useEffect, useState } from "react";
import TextField from "@mui/material/TextField";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import type { Loan } from "../../../types/Loan";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import MenuItem from "@mui/material/MenuItem";
import { useGetClientsQuery, useGetGamesQuery } from "../../../redux/services/ludotecaApi";
import type { Game } from "../../../types/Game";
import { Button } from "@mui/material";
import { LoaderContext } from "../../../context/LoaderProvider";
import type { Client } from "../../../types/Client";
import { Provider } from "react-redux";
import { store } from "../../../redux/store";
import moment from "moment";

interface Props {
    closeModal: () => void;
    create: (loan: Loan) => void;
}

const initialState = {
    id: "",
    game: "",
    client: "",
    startDate: "",
    endDate: "",
};

export default function CreateLoan(props: Props) {
    const [form, setForm] = useState(initialState);
    const [dateError, setDateError] = useState("");
    const loader = useContext(LoaderContext);
    const { data: games, isLoading: isLoadingCategories } =
        useGetGamesQuery({});

    const { data: clients, isLoading: isLoadingClients } = useGetClientsQuery(null);

    useEffect(() => {
        loader.showLoading(isLoadingCategories || isLoadingClients);
    }, [isLoadingCategories, isLoadingClients, loader]);

    const handleDateChange = (newStartDate: string, newEndDate: string) => {
        const periodStartDate = moment(newStartDate);
        const periodEndDate = moment(newEndDate);
        const period = periodEndDate.diff(periodStartDate, 'days');
        if (newStartDate > newEndDate) {
            setDateError("La fecha de devolución no puede ser anterior a la fecha de inicio.");
        } else if (period > 14) {
            setDateError("El período de préstamo no puede ser superior a 14 días.");
        }
        else {
            setDateError("");
        }
    }

    return (
        <div>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
                <Provider store={store}>

                    <Dialog open={true} onClose={props.closeModal}>
                        <DialogTitle>
                            Crear Préstamo
                        </DialogTitle>
                        <DialogContent>
                            <TextField
                                margin="dense"
                                disabled
                                id="id"
                                label="Id"
                                fullWidth
                                value={form.id}
                                variant="standard"
                            />
                            {/* {JSON.stringify(form)} */}
                            <TextField
                                margin="dense"
                                select
                                id="game"
                                label="Juego"
                                fullWidth
                                variant="standard"
                                onChange={(event) => {
                                    setForm({ ...form, game: event.target.value })
                                }}
                                value={form.game}
                            >
                                <MenuItem value="">Selecciona un juego</MenuItem>
                                {games &&
                                    games.map((option: Game) => (
                                        <MenuItem key={option.id} value={option.id}>
                                            {option.title}
                                        </MenuItem>
                                    ))}
                            </TextField>
                            <TextField
                                margin="dense"
                                id="client"
                                select
                                label="Cliente"
                                fullWidth
                                variant="standard"
                                onChange={(event) => {
                                    setForm({ ...form, client: event.target.value })
                                }}
                                value={form.client}
                            >
                                {clients &&
                                    clients.map((option: Client) => (
                                        <MenuItem key={option.id} value={option.id}>
                                            {option.name}
                                        </MenuItem>
                                    ))}</TextField>
                            <DatePicker
                                label="Fecha de Inicio"
                                format="dd/MM/yyyy"
                                onChange={(date) => {
                                    const newStartDate = date ? moment(date).format("YYYY-MM-DD") : "";
                                    setForm({
                                        ...form,
                                        startDate: newStartDate,
                                    });
                                    handleDateChange(newStartDate, form.endDate);
                                }}
                                slotProps={{
                                    textField: {
                                        margin: "dense",
                                        fullWidth: true,
                                        variant: "standard",
                                    }
                                }}
                            />
                            <DatePicker
                                label="Fecha de Devolución"
                                format="dd/MM/yyyy"
                                onChange={(date) => {
                                    const newEndDate = date ? moment(date).format("YYYY-MM-DD") : "";
                                    setForm({
                                        ...form,
                                        endDate: newEndDate,
                                    });
                                    handleDateChange(form.startDate, newEndDate);
                                    // const periodStartDate = moment(form.startDate);
                                    // const periodEndDate = moment(newEndDate);
                                    // const period = periodEndDate.diff(periodStartDate, 'days');
                                    // if (form.startDate > newEndDate) {
                                    //     setDateError("La fecha de devolución no puede ser anterior a la fecha de inicio.");
                                    // } else if (period > 14) {
                                    //     setDateError("El período de préstamo no puede ser superior a 14 días.");
                                    // }
                                    // else {
                                    //     setDateError("");
                                    // }
                                }}
                                slotProps={{
                                    textField: {
                                        margin: "dense",
                                        fullWidth: true,
                                        variant: "standard",
                                    }
                                }}
                            />
                        </DialogContent>
                        {dateError && (
                            <div style={{ color: 'red', marginLeft: '26px', width: '80%' }}>
                                {dateError}
                            </div>
                        )}
                        <DialogActions>
                            <Button onClick={props.closeModal}>Cancelar</Button>
                            <Button onClick={() => props.create({
                                id: form.id,
                                game: {
                                    id: form.game,
                                    title: "",
                                    age: 0
                                },
                                client: {
                                    id: form.client,
                                    name: ""
                                },
                                startDate: form.startDate,
                                endDate: form.endDate,
                            })}
                                disabled={!form.game || !form.client || !form.startDate || !form.endDate || !!dateError}
                            >
                                Crear
                            </Button>

                        </DialogActions>
                    </Dialog>
                </Provider>
            </LocalizationProvider>
        </div>
    );
}