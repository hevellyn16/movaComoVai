"use client";

import { useState } from "react";

const faqItems = [
  {
    question: "Como recupero minha senha?",
    answer:
      "Você pode redefinir sua senha pelo formulário de recuperação no login. Basta informar seu e-mail cadastrado para receber um link seguro.",
  },
  {
    question: "Onde encontro meus eventos salvos?",
    answer:
      "Seus eventos favoritos ficam disponíveis na aba favoritos. Você pode acessá-los a qualquer momento para conferir horários e detalhes.",
  },
];

export default function AjudaPage() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  return (
    <main className="flex-1 min-h-screen bg-[#F8F6F3] p-4 sm:p-6">
      <div className="mx-auto max-w-screen-2xl">
        <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h1 className="text-2xl font-bold text-mova-dark">Central de Ajuda</h1>
            <p className="text-sm text-gray-500">Como podemos ajudar? Encontre respostas rápidas e suporte para seu perfil e eventos.</p>
          </div>
        </div>

        <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
          <div className="space-y-6">
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-mova-dark">Como podemos ajudar?</h2>
                  <p className="text-sm text-gray-500">Busque por problemas, eventos e mais.</p>
                </div>
              </div>

              <div className="mt-6">
                <div className="flex items-center gap-3 rounded-3xl border border-gray-200 bg-gray-50 px-4 py-3 shadow-sm">
                  <span
                    className="material-symbols-outlined text-gray-400"
                    style={{ fontSize: 22 }}
                  >
                    search
                  </span>
                  <input
                    type="search"
                    placeholder="Busque por problemas, eventos..."
                    className="w-full bg-transparent text-sm text-gray-700 outline-none placeholder:text-gray-400"
                  />
                </div>
              </div>

            </section>

            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-mova-dark">Perguntas Frequentes</h2>
                  <p className="text-sm text-gray-500">Encontre respostas às dúvidas mais comuns.</p>
                </div>
              </div>

              <div className="mt-6 space-y-3">
                {faqItems.map((item, index) => {
                  const isOpen = openIndex === index;
                  return (
                    <div
                      key={item.question}
                      className="overflow-hidden rounded-3xl border border-gray-200 bg-gray-50"
                    >
                      <button
                        type="button"
                        onClick={() => setOpenIndex(isOpen ? null : index)}
                        className="cursor-pointer flex w-full items-center justify-between gap-4 px-5 py-4 text-left text-sm font-semibold text-mova-dark"
                      >
                        <span>{item.question}</span>
                        <span className="material-symbols-outlined text-gray-500">
                          {isOpen ? "expand_less" : "expand_more"}
                        </span>
                      </button>
                      {isOpen && (
                        <div className="px-5 pb-5 text-sm leading-6 text-gray-600">
                          {item.answer}
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            </section>

            <section className="overflow-hidden rounded-3xl border border-gray-200 bg-linear-to-r from-mova-red via-mova-yellow to-[#1F4E92] p-6 text-white shadow-sm">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm uppercase tracking-[0.2em] text-white/80">Sobral é cultura.</p>
                  <h3 className="mt-2 text-2xl font-semibold">Navegue pelas experiências que autenticam nossa cidade.</h3>
                  <p className="mt-3 max-w-xl text-sm text-white/90">
                    Encontre suporte e descubra como aproveitar o melhor da cena cultural com MoVa.
                  </p>
                </div>
              </div>

              <div className="mt-6 flex flex-wrap gap-2">
                <span className="rounded-full bg-white/15 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-white">
                  #CulturaLocal
</span>
                <span className="rounded-full bg-white/15 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-white">
                  #SuporteMoVa
</span>
              </div>
            </section>
          </div>

          <aside className="space-y-6">
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-mova-dark">Ainda com dúvidas?</h2>
              <p className="mt-2 text-sm text-gray-500">Nossa equipe de suporte está pronta para te ajudar a qualquer momento.</p>

              <div className="mt-6 space-y-3">
                <button className="cursor-pointer flex w-full items-center justify-between rounded-3xl bg-mova-red px-5 py-4 text-sm font-semibold text-white transition hover:bg-mova-red-dark">
                  <span>Iniciar Chat em Tempo Real</span>
                  <span className="material-symbols-outlined">chat</span>
                </button>
                <button className="cursor-pointer flex w-full items-center justify-between rounded-3xl border border-gray-200 bg-white px-5 py-4 text-sm font-semibold text-mova-dark transition hover:bg-gray-100">
                  <span>Enviar E-mail para Suporte</span>
                  <span className="material-symbols-outlined">mail</span>
                </button>
              </div>
            </section>

            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-mova-dark">Horário de Atendimento</h3>
              <div className="mt-4 space-y-4 text-sm text-gray-600">
                <div className="flex items-start gap-3">
                  <span className="material-symbols-outlined mt-1 text-mova-red">schedule</span>
                  <div>
                    <p className="font-semibold text-mova-dark">Segunda a Sexta</p>
                    <p>08:00 - 20:00</p>
                  </div>
                </div>
                <div className="flex items-start gap-3">
                  <span className="material-symbols-outlined mt-1 text-mova-yellow">calendar_month</span>
                  <div>
                    <p className="font-semibold text-mova-dark">Sábado</p>
                    <p>09:00 - 13:00</p>
                  </div>
                </div>
              </div>
            </section>
          </aside>
        </div>
      </div>
    </main>
  );
}
