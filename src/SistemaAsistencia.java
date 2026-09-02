import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class SistemaAsistencia {
        static List<Usuario> usuarios = new ArrayList();
        static List<Registro> registros = new ArrayList();
        static Usuario usuarioActual;
        static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        static JLabel reloj;

        public SistemaAsistencia() {
        }

        public static void main(String[] args) {
            cargarDatos();
            SwingUtilities.invokeLater(SistemaAsistencia::mostrarLogin);
        }

        static void cargarDatos() {
            usuarios.add(new Usuario(1, "Admin General", "admin@quimica.cl", "admin123", "Administrador"));
            usuarios.add(new Usuario(2, "Juan Pérez", "jperez@quimica.cl", "123456", "Empleado"));
            registros.add(new Registro(1, 2, "Entrada", "2026-08-30 09:45:00"));
            registros.add(new Registro(2, 2, "Salida", "2026-08-30 16:30:00"));
        }

        static void mostrarLogin() {
            JFrame frame = new JFrame("Empresa Química - Control de Asistencia");
            frame.setDefaultCloseOperation(3);
            frame.setSize(430, 330);
            frame.setLocationRelativeTo((Component)null);
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(7, 7, 7, 7);
            c.fill = 2;
            JLabel titulo = new JLabel("Empresa Química", 0);
            titulo.setFont(new Font("Segoe UI", 1, 22));
            titulo.setForeground(new Color(43, 108, 176));
            JLabel subtitulo = new JLabel("Control de Asistencia (MVP)", 0);
            subtitulo.setForeground(Color.GRAY);
            JTextField email = new JTextField("admin@quimica.cl");
            JPasswordField pass = new JPasswordField("admin123");
            JButton ingresar = new JButton("Ingresar");
            JLabel error = new JLabel("Credenciales incorrectas", 0);
            error.setForeground(Color.RED);
            error.setVisible(false);
            c.gridx = 0;
            c.gridy = 0;
            panel.add(titulo, c);
            ++c.gridy;
            panel.add(subtitulo, c);
            ++c.gridy;
            panel.add(new JLabel("Correo Electrónico"), c);
            ++c.gridy;
            panel.add(email, c);
            ++c.gridy;
            panel.add(new JLabel("Contraseña"), c);
            ++c.gridy;
            panel.add(pass, c);
            ++c.gridy;
            panel.add(ingresar, c);
            ++c.gridy;
            panel.add(error, c);
            ingresar.addActionListener((e) -> {
                String correo = email.getText().trim();
                String clave = new String(pass.getPassword());
                Usuario encontrado = (Usuario)usuarios.stream().filter((u) -> u.email.equals(correo) && u.pass.equals(clave)).findFirst().orElse((Usuario) null);
                if (encontrado != null) {
                    usuarioActual = encontrado;
                    frame.dispose();
                    mostrarAplicacion();
                } else {
                    error.setVisible(true);
                }

            });
            frame.add(panel);
            frame.setVisible(true);
        }

        static void mostrarAplicacion() {
            JFrame frame = new JFrame("Sistema de Asistencia - Empresa Química");
            frame.setDefaultCloseOperation(3);
            frame.setSize(1000, 720);
            frame.setLocationRelativeTo((Component)null);
            JPanel principal = new JPanel(new BorderLayout());
            JPanel nav = new JPanel(new BorderLayout());
            nav.setBackground(new Color(26, 32, 44));
            nav.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            JLabel titulo = new JLabel("Sistema de Asistencia");
            titulo.setForeground(Color.WHITE);
            titulo.setFont(new Font("Segoe UI", 1, 20));
            JLabel usuario = new JLabel(usuarioActual.nombre + " | " + usuarioActual.rol);
            usuario.setForeground(Color.WHITE);
            JButton cerrar = new JButton("Cerrar Sesión");
            cerrar.addActionListener((e) -> {
                frame.dispose();
                usuarioActual = null;
                mostrarLogin();
            });
            JPanel derecha = new JPanel(new FlowLayout(2));
            derecha.setOpaque(false);
            derecha.add(usuario);
            derecha.add(cerrar);
            nav.add(titulo, "West");
            nav.add(derecha, "East");
            principal.add(nav, "North");
            JPanel contenido = new JPanel();
            contenido.setLayout(new BoxLayout(contenido, 1));
            contenido.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            JPanel asistencia = new JPanel(new FlowLayout(1, 15, 15));
            asistencia.setBorder(BorderFactory.createTitledBorder("CA-01: Control de Asistencia"));
            reloj = new JLabel();
            reloj.setFont(new Font("Segoe UI", 1, 24));
            JButton entrada = new JButton("Marcar Entrada");
            JButton salida = new JButton("Marcar Salida");
            entrada.addActionListener((e) -> registrarAsistencia("Entrada"));
            salida.addActionListener((e) -> registrarAsistencia("Salida"));
            asistencia.add(reloj);
            asistencia.add(entrada);
            asistencia.add(salida);
            contenido.add(asistencia);
            if (usuarioActual.rol.equals("Administrador")) {
                contenido.add(crearPanelUsuarios());
                contenido.add(crearPanelReportes());
            }

            JScrollPane scroll = new JScrollPane(contenido);
            principal.add(scroll, "Center");
            frame.add(principal);
            frame.setVisible(true);
            Timer timer = new Timer(1000, (e) -> reloj.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            timer.start();
        }

        static JPanel crearPanelUsuarios() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Gestión de Usuarios (GU-01, GU-02, GU-03)"));
            JPanel formulario = new JPanel(new GridLayout(2, 5, 8, 8));
            JTextField nombre = new JTextField();
            JTextField email = new JTextField();
            JTextField pass = new JTextField();
            JComboBox<String> rol = new JComboBox(new String[]{"Empleado", "Administrador"});
            JButton guardar = new JButton("Guardar Usuario");
            formulario.add(new JLabel("Nombre"));
            formulario.add(new JLabel("Correo"));
            formulario.add(new JLabel("Contraseña"));
            formulario.add(new JLabel("Rol"));
            formulario.add(new JLabel(""));
            formulario.add(nombre);
            formulario.add(email);
            formulario.add(pass);
            formulario.add(rol);
            formulario.add(guardar);
            String[] columnas = new String[]{"ID", "Nombre", "Correo", "Rol", "Acciones"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable tabla = new JTable(modelo);
            cargarTablaUsuarios(modelo);
            guardar.addActionListener((e) -> {
                if (!nombre.getText().trim().isEmpty() && !email.getText().trim().isEmpty() && !pass.getText().trim().isEmpty()) {
                    int nuevoId = usuarios.stream().mapToInt((u) -> u.id).max().orElse(0) + 1;
                    usuarios.add(new Usuario(nuevoId, nombre.getText().trim(), email.getText().trim(), pass.getText().trim(), rol.getSelectedItem().toString()));
                    nombre.setText("");
                    email.setText("");
                    pass.setText("");
                    rol.setSelectedIndex(0);
                    cargarTablaUsuarios(modelo);
                } else {
                    JOptionPane.showMessageDialog(panel, "Completa todos los campos.", "Error", 0);
                }
            });
            JPanel acciones = new JPanel(new FlowLayout(2));
            JButton eliminar = new JButton("Eliminar Usuario");
            acciones.add(eliminar);
            eliminar.addActionListener((e) -> {
                int fila = tabla.getSelectedRow();
                if (fila == -1) {
                    JOptionPane.showMessageDialog(panel, "Selecciona un usuario de la tabla.");
                } else {
                    int id = (Integer)modelo.getValueAt(fila, 0);
                    if (id == usuarioActual.id) {
                        JOptionPane.showMessageDialog(panel, "No puedes eliminar el usuario actualmente conectado.", "Error", 0);
                    } else {
                        int confirmar = JOptionPane.showConfirmDialog(panel, "¿Confirmar eliminación?", "Eliminar usuario", 0);
                        if (confirmar == 0) {
                            usuarios.removeIf((u) -> u.id == id);
                            cargarTablaUsuarios(modelo);
                        }

                    }
                }
            });
            panel.add(formulario, "North");
            panel.add(new JScrollPane(tabla), "Center");
            panel.add(acciones, "South");
            return panel;
        }

        static void cargarTablaUsuarios(DefaultTableModel modelo) {
            modelo.setRowCount(0);

            for(Usuario u : usuarios) {
                modelo.addRow(new Object[]{u.id, u.nombre, u.email, u.rol, "Seleccionar para eliminar"});
            }

        }

        static JPanel crearPanelReportes() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Reportes del Sistema"));
            JPanel botones = new JPanel(new FlowLayout(0));
            JButton atrasos = new JButton("Atrasos (RE-01)");
            JButton salidas = new JButton("Salidas Anticipadas (RE-02)");
            JButton inasistencias = new JButton("Inasistencias (RE-03)");
            JTextArea resultado = new JTextArea(8, 70);
            resultado.setEditable(false);
            resultado.setLineWrap(true);
            resultado.setWrapStyleWord(true);
            atrasos.addActionListener((e) -> resultado.setText(generarReporte("atrasos")));
            salidas.addActionListener((e) -> resultado.setText(generarReporte("salidas")));
            inasistencias.addActionListener((e) -> resultado.setText(generarReporte("inasistencias")));
            botones.add(atrasos);
            botones.add(salidas);
            botones.add(inasistencias);
            panel.add(botones, "North");
            panel.add(new JScrollPane(resultado), "Center");
            return panel;
        }

        static void registrarAsistencia(String accion) {
            if (usuarioActual != null) {
                int nuevoId = registros.stream().mapToInt((r) -> r.id).max().orElse(0) + 1;
                String fecha = LocalDateTime.now().format(FORMATO);
                registros.add(new Registro(nuevoId, usuarioActual.id, accion, fecha));
                JOptionPane.showMessageDialog((Component)null, "Marca de " + accion + " registrada exitosamente.\n" + fecha, "Asistencia", 1);
            }
        }

        static String generarReporte(String tipo) {
            StringBuilder sb = new StringBuilder();
            if (tipo.equals("atrasos")) {
                sb.append(generarReporteAtrasos());
            } else if (tipo.equals("salidas")) {
                sb.append("RE-02: REPORTE DE SALIDAS ANTICIPADAS (< 17:30 PM)\n\n");
                boolean hay = false;

                for(Registro r : registros) {
                    if (r.accion.equals("Salida")) {
                        String hora = r.fechaHora.substring(11, 16);
                        if (hora.compareTo("17:30") < 0) {
                            Usuario u = buscarUsuario(r.userId);
                            sb.append("- ").append(u != null ? u.nombre : "ID #" + r.userId).append(" | ").append(r.fechaHora).append("\n");
                            hay = true;
                        }
                    }
                }

                if (!hay) {
                    sb.append("No se registran salidas anticipadas.");
                }
            } else {
                sb.append("RE-03: REPORTE DE INASISTENCIAS\n\n");
                boolean hay = false;

                for(Usuario u : usuarios) {
                    boolean tieneRegistro = registros.stream().anyMatch((rx) -> rx.userId == u.id);
                    if (!tieneRegistro) {
                        sb.append("- ").append(u.nombre).append(" (ID: ").append(u.id).append(") - Sin actividad registrada.\n");
                        hay = true;
                    }
                }

                if (!hay) {
                    sb.append("Todos los usuarios tienen actividad registrada.");
                }
            }

            return sb.toString();
        }

        static String generarReporteAtrasos() {
            String HORA_LIMITE = "09:30";
            StringBuilder sb = new StringBuilder();
            sb.append("RE-01: REPORTE DE ATRASOS (entradas posteriores a las 09:30)\n");
            sb.append("--------------------------------------------------------------\n\n");
            Map<Integer, List<String>> atrasosPorUsuario = new LinkedHashMap();

            for(Registro r : registros) {
                if (r.accion.equals("Entrada")) {
                    String hora = r.fechaHora.substring(11, 16);
                    if (hora.compareTo("09:30") > 0) {
                        ((List)atrasosPorUsuario.computeIfAbsent(r.userId, (k) -> new ArrayList())).add(r.fechaHora);
                    }
                }
            }

            if (atrasosPorUsuario.isEmpty()) {
                sb.append("No se registran entradas atrasadas.");
                return sb.toString();
            } else {
                for(Map.Entry<Integer, List<String>> entry : atrasosPorUsuario.entrySet()) {
                    int userId = (Integer)entry.getKey();
                    List<String> fechas = (List)entry.getValue();
                    Usuario u = buscarUsuario(userId);
                    sb.append("Usuario ID: ").append(userId).append(" | Nombre: ").append(u != null ? u.nombre : "(usuario eliminado)").append(" | Total de días atrasado: ").append(fechas.size()).append("\n");

                    for(String fecha : fechas) {
                        String horaEntrada = fecha.substring(11, 16);
                        sb.append("      - ").append(fecha).append("  (entró a las ").append(horaEntrada).append(")\n");
                    }

                    sb.append("\n");
                }

                return sb.toString();
            }
        }

        static Usuario buscarUsuario(int id) {
            return (Usuario)usuarios.stream().filter((u) -> u.id == id).findFirst().orElse((Usuario) null);
        }

        static class Usuario {
            int id;
            String nombre;
            String email;
            String pass;
            String rol;

            Usuario(int id, String nombre, String email, String pass, String rol) {
                this.id = id;
                this.nombre = nombre;
                this.email = email;
                this.pass = pass;
                this.rol = rol;
            }
        }

        static class Registro {
            int id;
            int userId;
            String accion;
            String fechaHora;

            Registro(int id, int userId, String accion, String fechaHora) {
                this.id = id;
                this.userId = userId;
                this.accion = accion;
                this.fechaHora = fechaHora;
            }
        }
}
