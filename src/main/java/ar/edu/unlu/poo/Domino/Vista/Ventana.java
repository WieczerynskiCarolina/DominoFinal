package ar.edu.unlu.poo.Domino.Vista;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import ar.edu.unlu.poo.Domino.Modelo.EntradaRanking;
import ar.edu.unlu.poo.Domino.Modelo.Ficha;
import ar.edu.unlu.poo.Domino.Modelo.Jugador;
import ar.edu.unlu.poo.Domino.Modelo.Lado;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Ventana implements IVista{
    private Controlador controlador;
    private boolean mesaVaciaActual;
    private int indiceFichaSeleccionada = -1;

    @FXML private HBox contenedorMano;
    @FXML private HBox contenedorMesa;
    @FXML private Button btnFlechaIzquierda;
    @FXML private Button btnFlechaDerecha;
    @FXML private Label lblTurno;
    @FXML private Label lblEstadoPozo;
    @FXML private Label lblMensajes;
    @FXML private Button btnJugar;
    @FXML private Button btnPasar;
    @FXML private TextArea txtAreaLobby;
    @FXML private VBox panelLobby;
    @FXML private VBox panelJuego;
    @FXML private VBox panelResultados;
    @FXML private Label lblTituloResultado;
    @FXML private TextArea listaPuntajes;
    @FXML private Label lblRankingTop5;
    @FXML private Button btnContinuarRonda;
    @FXML private Button btnNuevaPartida;
    @FXML private Button btnSalir;
    @FXML private VBox cajaTop5;
    @FXML private Label lblErrorLobby;

    private enum Pantalla{
        LOBBY, JUEGO, RESULTADOS
    }

    @Override
    public void setControlador(Controlador controlador){
        this.controlador = controlador;
    }

    @Override
    public void iniciar(){
        Platform.runLater(() -> {
            String nombre = pedirNombreJugador();
            if(nombre != null && !nombre.trim().isEmpty()){
                controlador.conectar(nombre);
            } else{
                mostrarMensaje("El nombre no puede estar vacío. Reinicie la aplicación.");
            }
        });
    }

    @Override
    public void mostrarMensaje(String mensaje){
        Platform.runLater(() -> {
            // Actualiza el label del juego (si la partida ya empezó)
            if (lblMensajes != null) {
                lblMensajes.setText(mensaje);
            }

            // Actualiza el label del lobby (si todavía están esperando/conectando)
            if (lblErrorLobby != null) {
                // Si es un mensaje normal de conexión, lo ponemos en blanco o celeste.
                // Si contiene la palabra "Error" o "ya comenzó", lo dejamos en rojo.
                if (mensaje.toLowerCase().contains("error") || mensaje.toLowerCase().contains("no puedes")) {
                    lblErrorLobby.setStyle("-fx-text-fill: #ff5555; -fx-font-size: 16px; -fx-font-weight: bold;");
                } else {
                    lblErrorLobby.setStyle("-fx-text-fill: #00ffff; -fx-font-size: 16px; -fx-font-weight: bold;");
                }
                lblErrorLobby.setText(mensaje);
            }
        });
    }

    @Override
    public void mostrarLobby(List<String> nombres, int totalRequeridos){

        cambiarPantalla(Pantalla.LOBBY);

        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Conectados: ").append(nombres.size()).append(" / ").append(totalRequeridos).append("\n\n");
            for(String n: nombres){
                sb.append(" - ").append(n).append("\n");
            }
            txtAreaLobby.setText(sb.toString());
        });
    }

    @Override
    public void mostrarJuego(String jugadorActual, List<Ficha> fichasMesa, ArrayList<Ficha> mano, boolean pozoVacio) {

        cambiarPantalla(Pantalla.JUEGO);

        Platform.runLater(() -> {
            // Actualizo textos informativos
            lblTurno.setText("Turno de: " + jugadorActual);
            lblEstadoPozo.setText("Pozo: " + (pozoVacio ? "VACÍO" : "Fichas disponibles"));

            // Limpio las cajas horizontales (HBox) antes de volver a dibujar
            contenedorMesa.getChildren().clear();
            contenedorMano.getChildren().clear();

            // ----------------------------------------------------
            // DIBUJAR LA MESA (En el centro de la pantalla)
            // ----------------------------------------------------
            if (fichasMesa != null) {
                for (Ficha f : fichasMesa) {
                    ImageView imgMesa = cargarImagenFicha(f);

                    if (f.getValorA() == f.getValorB()) {
                        // Los dobles quedan verticales
                        imgMesa.setRotate(0);
                    } else {
                        // Fichas normales van horizontales
                        // Asumo que getValorA() debe ir visualmente a la izquierda.
                        if (f.getValorA() < f.getValorB()) {
                            // roto el menor a la izquierda.
                            // -90 rota la parte de arriba (menor) hacia la izquierda.
                            imgMesa.setRotate(-90);
                        } else {
                            // el mayor a la izquierda.
                            // 90 rota la parte de abajo (mayor) hacia la izquierda.
                            imgMesa.setRotate(90);
                        }
                    }

                    // Envuelvo la imagen en un Group
                    // Esto fuerza al HBox a respetar el ancho real de la ficha acostada
                    javafx.scene.Group fichaRotada = new javafx.scene.Group(imgMesa);

                    // Agregamos el margen al Group en lugar del ImageView
                    HBox.setMargin(fichaRotada, new javafx.geometry.Insets(0, 0, 0, 0));
                    contenedorMesa.getChildren().add(fichaRotada);
                }
            }

            // ----------------------------------------------------
            // DIBUJAR LA MANO (Con resaltado interactivo)
            // ----------------------------------------------------
            if (mano != null) {
                List<Integer> jugables = new ArrayList<>();
                try {
                    jugables = controlador.obtenerIndicesJugables();
                } catch (Exception e) {
                    System.out.println("Error al obtener fichas jugables: " + e.getMessage());
                }

                for (int i = 0; i < mano.size(); i++) {
                    Ficha f = mano.get(i);
                    ImageView imgMano = cargarImagenFicha(f);

                    final int indexSeleccionado = i;

                    if (jugables.contains(i)) {
                        imgMano.setEffect(new Glow(0.6));
                        imgMano.setStyle("-fx-cursor: hand;");

                        /*imgMano.setOnMouseClicked(event -> seleccionarFicha(indexSeleccionado));*/
                        imgMano.setOnMouseClicked(event -> {
                            resaltarFichaSeleccionada(imgMano); // Primero la iluminamos en pantalla
                            seleccionarFicha(indexSeleccionado); // Luego le avisamos a la lógica interna
                        });

                    } else {
                        imgMano.setOpacity(0.4);
                    }

                    HBox.setMargin(imgMano, new javafx.geometry.Insets(0, 5, 0, 5));
                    contenedorMano.getChildren().add(imgMano);
                }
            }
        });
    }

    private ImageView cargarImagenFicha(Ficha f) {
        int valor1 = Math.min(f.getValorA(), f.getValorB());
        int valor2 = Math.max(f.getValorA(), f.getValorB());

        String nombreArchivo = valor1 + "-" + valor2 + ".png";
        String ruta = "/com/example/dominotpf2/imagenes/fichas/" + nombreArchivo;

        // control por consola
        //System.out.println("Intentando cargar: " + ruta);

        // Buscamos el archivo de forma segura
        java.io.InputStream inputStream = getClass().getResourceAsStream(ruta);

        if (inputStream == null) {
            System.out.println("¡ERROR! No se encontró la imagen en: " + ruta);
            return new ImageView();
        }

        // Si la encontró, armamos la imagen normal
        Image imagen = new Image(inputStream);
        ImageView imageView = new ImageView(imagen);

        imageView.setFitWidth(30);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    private void seleccionarFicha(int index){
        this.indiceFichaSeleccionada = index;
        if (mesaVaciaActual) {
            // Si está vacía, juega directo a la derecha (centro)
            controlador.intentarJugarFicha(index, Lado.DERECHA);
        } else {
            // Mostramos las flechas para que elija el lado
            btnFlechaIzquierda.setVisible(true);
            btnFlechaDerecha.setVisible(true);
            mostrarMensaje("¿En qué extremo quieres colocarla?");
        }
    }

    @Override
    public void activarControles(boolean mesaVacia){
        this.mesaVaciaActual = mesaVacia;
        Platform.runLater(() -> {
            btnJugar.setDisable(false);
            btnPasar.setDisable(false);
        });
    }

    @Override
    public void desactivarControles(){
        Platform.runLater(() -> {
            btnJugar.setDisable(true);
            btnPasar.setDisable(true);
        });
    }


    private void cambiarPantalla(Pantalla pantalla) {
        Platform.runLater(() -> {
            // Apagamos todos
            panelLobby.setVisible(false); panelLobby.setManaged(false);
            panelJuego.setVisible(false); panelJuego.setManaged(false);
            panelResultados.setVisible(false); panelResultados.setManaged(false);

            // Prendemos solo el que nos piden
            switch (pantalla) {
                case LOBBY -> { panelLobby.setVisible(true); panelLobby.setManaged(true); }
                case JUEGO -> { panelJuego.setVisible(true); panelJuego.setManaged(true); }
                case RESULTADOS -> { panelResultados.setVisible(true); panelResultados.setManaged(true); }
            }
        });
    }

    private void resaltarFichaSeleccionada(ImageView fichaElegida) {
        // Recorremos todas las fichas de la mano para "limpiarlas"
        for (javafx.scene.Node nodo : contenedorMano.getChildren()) {
            ImageView img = (ImageView) nodo;


            if (img.getOpacity() == 1.0) {
                img.setEffect(new Glow(0.6));
            }
        }

        // efecto de borde luminoso
        DropShadow bordeNeon = new DropShadow();
        bordeNeon.setColor(Color.CYAN);
        bordeNeon.setRadius(15);
        bordeNeon.setSpread(0.6);

        fichaElegida.setEffect(bordeNeon);
    }

    @FXML
    public void initialize() {
        panelLobby.setVisible(true);
        panelLobby.setManaged(true);

        panelJuego.setVisible(false);
        panelJuego.setManaged(false);

        panelResultados.setVisible(false);
        panelResultados.setManaged(false);

        txtAreaLobby.setText("Conectando con el servidor...\nPor favor, ingresa tu nombre.");

        try {
            String rutaLobby = getClass().getResource("/com/example/dominotpf2/imagenes/fondos/fondo_lobby.png").toExternalForm();
            String rutaJuego = getClass().getResource("/com/example/dominotpf2/imagenes/fondos/fondo_juego.png").toExternalForm();

            panelLobby.setStyle("-fx-background-image: url('" + rutaLobby + "'); " +
                    "-fx-background-size: 100% 100%; " +
                    "-fx-background-position: center center;");

            panelJuego.setStyle("-fx-background-image: url('" + rutaJuego + "'); " +
                    "-fx-background-size: 100% 100%; " +
                    "-fx-background-position: center center;");

            panelResultados.setStyle("-fx-background-image: url('" + rutaLobby + "'); " +
                    "-fx-background-size: 100% 100%; " +
                    "-fx-background-position: center center;");

        } catch (Exception e) {
            System.out.println("Aviso: No se encontraron las imágenes de fondo.");
        }

        lblTituloResultado.setText("");
        listaPuntajes.setText("");
        lblRankingTop5.setText("");
        btnContinuarRonda.setVisible(false);
        btnNuevaPartida.setVisible(false);
        btnSalir.setVisible(false);

        btnContinuarRonda.setOnAction(event -> {
            controlador.confirmarFinRonda();
        });

        btnSalir.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        btnNuevaPartida.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("100");
            dialog.setTitle("Nueva Partida");
            dialog.setHeaderText("Configurar nueva partida");
            dialog.setContentText("Ingrese el nuevo puntaje límite:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int nuevoLimite = Integer.parseInt(result.get());
                    btnNuevaPartida.setVisible(false);
                    btnSalir.setVisible(false);
                    controlador.solicitarNuevaPartida(nuevoLimite);
                } catch (NumberFormatException e) {
                    mostrarMensaje("Por favor, ingrese un número válido.");
                }
            }
        });
    }

    @FXML
    private void accionPasar() {
        controlador.intentarPasar();
    }

    @FXML
    private void clickFlechaIzquierda() {
        confirmarJugada(Lado.IZQUIERDA);
    }

    @FXML
    private void clickFlechaDerecha() {
        confirmarJugada(Lado.DERECHA);
    }

    private void confirmarJugada(Lado lado) {
        controlador.intentarJugarFicha(indiceFichaSeleccionada, lado);
        btnFlechaIzquierda.setVisible(false);
        btnFlechaDerecha.setVisible(false);
    }

    @Override
    public String pedirNombreJugador() {
        TextInputDialog dialog = new TextInputDialog("Jugador");
        dialog.setTitle("Nombre");
        dialog.setHeaderText("Bienvenido al Dominó");
        dialog.setContentText("Ingrese su nombre:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse("Anónimo");
    }

    /*@Override
    public void mostrarFinRonda(String ganador, int puntos) {
        cambiarPantalla(Pantalla.RESULTADOS);

        Platform.runLater(() -> {
            String msg = (ganador != null)
                    ? "¡GANADOR DE LA RONDA: " + ganador.toUpperCase() + "! (+ " + puntos + " puntos)"
                    : "¡JUEGO CERRADO! Nadie pudo mover.";
            lblTituloResultado.setText(msg);

            btnContinuarRonda.setVisible(false);
            lblRankingTop5.setText("");
        });
    }*/

    @Override
    public void mostrarFinRonda(String ganador, int puntos) {
        cambiarPantalla(Pantalla.RESULTADOS);
        Platform.runLater(() -> {
            String msg = (ganador != null)
                    ? "¡GANADOR DE LA RONDA: " + ganador.toUpperCase() + "! (+ " + puntos + " puntos)"
                    : "¡JUEGO CERRADO! Nadie pudo mover.";
            lblTituloResultado.setText(msg);

            btnContinuarRonda.setVisible(false);

            cajaTop5.setVisible(false);
            cajaTop5.setManaged(false);
        });
    }

    @Override
    public void mostrarPuntajesTotales(List<Jugador> jugadores) {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            for(Jugador j : jugadores) {
                sb.append(j.getNombre()).append(": ").append(j.getPuntajeTotal()).append(" pts\n");
            }
            listaPuntajes.setText(sb.toString());
        });
    }

    @Override
    public void solicitarContinuarRonda(){
        Platform.runLater(() -> {
            btnContinuarRonda.setVisible(true);
        });
    }

    /*@Override
    public void mostrarCierreJuego() {
        Platform.runLater(() -> {
            lblTituloResultado.setText("El ganador cerró la mesa.\n¡Gracias por jugar!");
            lblRankingTop5.setText("");
        });
    }*/

    @Override
    public void mostrarCierreJuego() {
        Platform.runLater(() -> {
            lblTituloResultado.setText("El ganador cerró la mesa.\n¡Gracias por jugar!");
        });
    }

    /*@Override
    public void mostrarGanadorJuego(String nombreGanador, boolean soyElGanador) {
        cambiarPantalla(Pantalla.RESULTADOS);
        Platform.runLater(() -> {
            lblTituloResultado.setText("¡FIN DEL JUEGO!\nGanador de la partida: " + nombreGanador.toUpperCase());
            btnContinuarRonda.setVisible(false);

            btnSalir.setVisible(true);
            btnNuevaPartida.setVisible(soyElGanador);

            btnSalir.setOnAction(event -> {
                if (soyElGanador) {
                    controlador.cerrarMesa();
                }
                Platform.exit();
                System.exit(0);
            });
        });
    }*/

    @Override
    public void mostrarGanadorJuego(String nombreGanador, boolean soyElGanador) {
        cambiarPantalla(Pantalla.RESULTADOS);
        Platform.runLater(() -> {
            lblTituloResultado.setText("¡FIN DEL JUEGO!\nGanador de la partida: " + nombreGanador.toUpperCase());
            btnContinuarRonda.setVisible(false);

            btnSalir.setVisible(true);
            btnNuevaPartida.setVisible(soyElGanador);

            // Volvemos a prender la columna del Top 5
            cajaTop5.setVisible(true);
            cajaTop5.setManaged(true);

            btnSalir.setOnAction(event -> {
                if (soyElGanador) {
                    controlador.cerrarMesa();
                }
                new Thread(() -> {
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                    Platform.runLater(() -> Platform.exit());
                    System.exit(0);
                }).start();
            });
        });
    }

    @Override
    public void mostrarRanking(List<EntradaRanking> ranking) {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            for(EntradaRanking er : ranking) {
                sb.append(er.toString()).append("\n");
            }
            lblRankingTop5.setText(sb.toString());
        });
    }
}
