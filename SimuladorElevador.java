import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class SimuladorElevador implements ActionListener {

    // Variáveis globais
    JFrame frame;
    JButton calcularBtn, resetBtn;
    JTextField campoPessoas, campoVelocidadeInicial, campoAceleracao, campoTempo,
            campoVelocidadeFinal, campoPesoTotal;

    // Constantes físicas
    private static final double MASSA_ELEVADOR = 500.0; // kg
    private static final double MASSA_PESSOA = 70.0;   // kg
    private static final double GRAVIDADE = 9.81;     // m/s²
    private static final int MAX_PESSOAS = 15;

    // Formatador para números
    private final DecimalFormat df = new DecimalFormat("0.00");

    // Descrições dos resultados
    String velocidadeFinalDescricao, pesoTotalDescricao;

    // Construtor
    SimuladorElevador() {
        // Cria a janela principal
        frame = new JFrame("Calcula Dados do Elevador");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(430, 400);

        frame.setLayout(null);

        // Cria os títulos e inputs para as variáveis
        // Número de pessoas
        JLabel pessoasText = createLabel("Número de pessoas:", 40, 25, 160, 30);
        campoPessoas = createTextField("", 200, 25, 70, 30, true, true);

        // Velocidade inicial
        JLabel viText = createLabel("Velocidade inicial (m/s):", 40, 60, 160, 30);
        campoVelocidadeInicial = createTextField("", 200, 60, 70, 30, true, true);

        // Aceleração
        JLabel aceleracaoText = createLabel("Aceleração (m/s²):", 40, 95, 160, 30);
        campoAceleracao = createTextField("", 200, 95, 70, 30, true, true);

        // Tempo
        JLabel tempoText = createLabel("Tempo (s):", 40, 130, 160, 30);
        campoTempo = createTextField("", 200, 130, 70, 30, true, true);

        // Botão para calcular
        calcularBtn = new JButton("Calcular");
        calcularBtn.setBounds(140, 290, 80, 30);
        calcularBtn.addActionListener(this);
        calcularBtn.setFocusable(false);

        // Cria as variáveis de saída
        velocidadeFinalDescricao = "Velocidade final (m/s): ";
        campoVelocidadeFinal = createTextField(velocidadeFinalDescricao, 40, 190, 350, 30, false, false);

        pesoTotalDescricao = "Peso total (N): ";
        campoPesoTotal = createTextField(pesoTotalDescricao, 40, 230, 350, 30, false, false);

        // Botão para resetar
        resetBtn = new JButton("Reset");
        resetBtn.setBounds(230, 290, 80, 30);
        resetBtn.addActionListener(this);
        resetBtn.setFocusable(false);

        // Adicionar elementos ao frame para mostrar na tela
        frame.add(pessoasText);
        frame.add(viText);
        frame.add(aceleracaoText);
        frame.add(tempoText);
        frame.add(campoPessoas);
        frame.add(campoVelocidadeInicial);
        frame.add(campoAceleracao);
        frame.add(campoTempo);
        frame.add(campoVelocidadeFinal);
        frame.add(campoPesoTotal);
        frame.add(calcularBtn);
        frame.add(resetBtn);
        frame.setVisible(true);
    }

    // Função Principal que roda o programa
    public static void main(String[] args) {
        new SimuladorElevador();
    }

    // Executa a função dos botões
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calcularBtn) {
            try {
                // Obter e validar entradas
                String strPessoas = campoPessoas.getText().trim();
                String strVI = campoVelocidadeInicial.getText().trim();
                String strAceleracao = campoAceleracao.getText().trim();
                String strTempo = campoTempo.getText().trim();

                // Verificar campos vazios
                if (strPessoas.isEmpty() || strVI.isEmpty() || strAceleracao.isEmpty() || strTempo.isEmpty()) {
                    errorMsg("Todos os campos devem ser preenchidos", "Campos vazios");
                    return;
                }

                // Converter valores para números
                int pessoas = Integer.parseInt(strPessoas);
                double v0 = Double.parseDouble(strVI);
                double a = Double.parseDouble(strAceleracao);
                double t = Double.parseDouble(strTempo);

                // Validar valores para verificar se são possíveis
                if (pessoas < 0 || pessoas > MAX_PESSOAS) {
                    errorMsg("O número de pessoas deve estar entre 0 e " + MAX_PESSOAS, "Erro de entrada");
                    return;
                }

                if (t < 1) {
                    errorMsg("O tempo não pode ser negativo e nem menor que 1", "Erro de entrada");
                    return;
                }

                if (t > 150) {
                    errorMsg("O tempo é muito longo (máximo 150 segundos)", "Erro de entrada");
                    return;
                }

                // Limite de velocidade inicial para elevadores comuns
                if (v0 < -10 || v0 > 20) {
                    errorMsg("A velocidade inicial deve estar entre -10 e 20 m/s", "Erro de entrada");
                    return;
                }

                // Limite de aceleração para elevadores
                if (a < -1 || a > 2) {
                    errorMsg("A aceleração deve estar entre -1 e 2 m/s²", "Erro de entrada");
                    return;
                }

                // Cálculos físicos
                double massaTotal = MASSA_ELEVADOR + (pessoas * MASSA_PESSOA);
                double pesoTotal;
                if (a > 0) {
                    // Aceleração para cima: peso aparente aumenta
                    pesoTotal = massaTotal * (GRAVIDADE + a);
                } else if (a < 0) {
                    // Aceleração para baixo: peso aparente diminui
                    pesoTotal = massaTotal * (GRAVIDADE - Math.abs(a));
                } else {
                    // Sem aceleração: peso normal
                    pesoTotal = massaTotal * GRAVIDADE;
                }
                double vf = v0 + (a * t); // Fórmula da velocidade final

                // Verificar se a velocidade final é humanamente possível
                if (vf < -15 || vf > 15) {
                    errorMsg("A velocidade final calculada (" + df.format(vf) +
                            " m/s) excede os limites de segurança (-15 a 15 m/s).", "Erro de entrada");
                    return;
                }

                // Exibir resultados
                String resultadoVF = createResultString(velocidadeFinalDescricao, vf);
                campoVelocidadeFinal.setText(resultadoVF);

                String resultadoPeso = createResultString(pesoTotalDescricao, pesoTotal);
                campoPesoTotal.setText(resultadoPeso);

            } catch (NumberFormatException ex) {
                errorMsg("Por favor, insira valores numéricos válidos", "Erro de entrada");
            }
        }

        // Executa a função do botão "Reset"
        if (e.getSource() == resetBtn) {
            // Limpar as caixas de texto
            campoPessoas.setText("");
            campoVelocidadeInicial.setText("");
            campoAceleracao.setText("");
            campoTempo.setText("");

            campoVelocidadeFinal.setText("");
            campoPesoTotal.setText("");
        }
    }

    // Método: Cria as caixas de input
    private JTextField createTextField(String label, int x, int y, int w, int h, boolean edit, boolean focus) {
        JTextField textField = new JTextField(label);
        textField.setBounds(x, y, w, h);
        textField.setEditable(edit);
        textField.setFocusable(focus);

        return textField;
    }

    // Método: Cria as Labels(titulos)
    private JLabel createLabel(String label, int x, int y, int w, int h) {
        JLabel labelText = new JLabel(label);
        labelText.setBounds(x, y, w, h);

        return labelText;
    }

    // Método: Cria a frase de resposta a partir de uma descrição e um número
    private String createResultString(String description, Double valor) {
        String string = description + df.format(valor);
        return string;
    }

    // Método: Mostra a mensagem de erro.
    private void errorMsg(String msg, String title) {
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}