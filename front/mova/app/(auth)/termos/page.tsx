"use client";

import Link from "next/link";

interface TermsSection {
  id: string;
  title: string;
  content: React.ReactNode;
}

const termsSections: TermsSection[] = [
  {
    id: "elegibilidade",
    title: "1. Elegibilidade e cadastro",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Para utilizar o MoVa, você deve:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed">
          <li>Ter 13 anos ou mais;</li>
          <li>Fornecer informações verdadeiras e atualizadas no cadastro;</li>
          <li>
            Manter a segurança da sua senha e ser responsável por toda atividade
            realizada na sua conta;
          </li>
          <li>
            Notificar imediatamente a equipe do MoVa em caso de uso não
            autorizado.
          </li>
        </ul>
      </>
    ),
  },
  {
    id: "uso",
    title: "2. Uso da plataforma",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          O MoVa é uma plataforma voltada à divulgação de eventos e à conexão
          com a cultura local. Você pode:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed mb-3">
          <li>Explorar e compartilhar eventos culturais em Sobral;</li>
          <li>Interagir com outros usuários de forma respeitosa;</li>
          <li>
            Publicar eventos, desde que as informações sejam precisas e
            verídicas.
          </li>
        </ul>
        <p className="text-gray-600 text-sm leading-relaxed">
          Não é permitido utilizar a plataforma para divulgar conteúdo falso,
          ofensivo, discriminatório ou que viole direitos de terceiros.
        </p>
      </>
    ),
  },
  {
    id: "privacidade",
    title: "3. Privacidade e dados",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Coletamos apenas os dados necessários para o funcionamento do serviço,
          como nome, e-mail e preferências culturais. Suas informações não serão
          compartilhadas com terceiros sem o seu consentimento, exceto quando
          exigido por lei.
        </p>
        <p className="text-gray-600 text-sm leading-relaxed">
          Ao usar o login com Google, você autoriza o MoVa a receber seu nome e
          e-mail cadastrados na conta Google para fins de autenticação.
        </p>
      </>
    ),
  },
  {
    id: "conteudo",
    title: "4. Conteúdo gerado pelo usuário",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Ao publicar eventos ou interagir na plataforma, você garante que possui
        os direitos sobre o conteúdo compartilhado e autoriza o MoVa a
        exibi-lo para outros usuários. O MoVa se reserva o direito de remover
        qualquer conteúdo que viole estas diretrizes.
      </p>
    ),
  },
  {
    id: "disponibilidade",
    title: "5. Disponibilidade do serviço",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        O MoVa é disponibilizado no estado em que se encontra. Podemos realizar
        manutenções, atualizações ou alterações nas funcionalidades a qualquer
        momento, com ou sem aviso prévio. Não garantimos disponibilidade
        ininterrupta da plataforma.
      </p>
    ),
  },
  {
    id: "alteracoes",
    title: "6. Alterações nestes termos",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Podemos atualizar estes Termos de Uso periodicamente. Quando isso
        ocorrer, notificaremos você por e-mail ou por meio de aviso na
        plataforma. O uso contínuo do MoVa após as alterações implica na
        aceitação dos novos termos.
      </p>
    ),
  },
  {
    id: "encerramento",
    title: "7. Encerramento de conta",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Você pode encerrar sua conta a qualquer momento nas configurações do
        perfil. O MoVa também pode suspender ou encerrar contas que violem
        estes termos, sem aviso prévio.
      </p>
    ),
  },
];

export default function TermsPage() {
  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 font-sans">
      <div className="max-w-2xl mx-auto bg-white rounded-xl border border-gray-200 shadow-sm p-8 md:p-12">

        {/* Header */}
        <div className="mb-8 pb-6 border-b border-gray-200">
          <span className="inline-block text-xs font-semibold text-gray-500 bg-gray-100 border border-gray-200 rounded-md px-2.5 py-1 mb-4 tracking-wide">
            Documento legal
          </span>
          <h1 className="text-4xl font-bold text-[#b91c1c] mb-1">MoVa</h1>
          <h2 className="text-xl font-semibold text-gray-800 mb-2">
            Termos de Uso
          </h2>
          <p className="text-xs text-gray-400">
            Última atualização: junho de 2026 &nbsp;·&nbsp; Versão 1.0
          </p>
        </div>

        {/* Intro */}
        <p className="text-sm text-gray-600 leading-relaxed mb-2">
          Bem-vindo ao <span className="font-semibold text-gray-800">MoVa</span>
          , plataforma de descoberta cultural da cidade de Sobral. Ao criar uma
          conta ou utilizar qualquer funcionalidade do nosso serviço, você
          concorda com os termos descritos neste documento. Leia com atenção
          antes de continuar.
        </p>

        {/* Sections */}
        <div className="mt-6 space-y-6">
          {termsSections.map((section) => (
            <div key={section.id}>
              <div className="border-t border-gray-100 pt-6">
                <h3 className="text-sm font-semibold text-gray-800 mb-3">
                  {section.title}
                </h3>
                {section.content}
              </div>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div className="mt-8 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-sm text-gray-600 leading-relaxed">
            Dúvidas sobre estes termos? Entre em contato pelo e-mail{" "}
            <a
              href="mailto:contato@mova.com.br"
              className="font-medium text-[#b91c1c] hover:underline"
            >
              contato@mova.com.br
            </a>
            . Ao continuar usando a plataforma, você confirma que leu e concorda
            com estes Termos de Uso.
          </p>
        </div>

        {/* Back link */}
        <div className="mt-6 text-center">
          <Link
            href="/register"
            className="text-sm text-[#0066cc] hover:underline font-medium"
          >
            ← Voltar para o cadastro
          </Link>
        </div>

      </div>
    </div>
  );
}