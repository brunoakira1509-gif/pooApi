/* Front-end do Pontec Vagas integrado à API REST (/api/vagas). */
(function () {
  "use strict";

  // Same origin quando servido pelo Spring Boot; localhost:8080 se aberto de outro lugar (ex.: file://).
  var API = (location.protocol === "http:" || location.protocol === "https:") && location.port === "8080"
    ? "/api/vagas"
    : "http://localhost:8080/api/vagas";

  var CORES = ["#2f6fd6", "#d6452f", "#7b2fd6", "#16794a", "#c2570c", "#0b78a8", "#d62f4f", "#455ca8"];

  var $ = function (id) { return document.getElementById(id); };
  var lista = $("lista");
  var vagaParaExcluir = null;

  /* ---------- Utilidades ---------- */
  function el(tag, cls, text) {
    var n = document.createElement(tag);
    if (cls) n.className = cls;
    if (text != null) n.textContent = text;
    return n;
  }

  function toast(msg, erro) {
    var t = el("div", "toast" + (erro ? " is-error" : ""), msg);
    $("toasts").appendChild(t);
    setTimeout(function () { t.classList.add("out"); }, 3000);
    setTimeout(function () { t.remove(); }, 3400);
  }

  function cor(nome) {
    var h = 0;
    for (var i = 0; i < nome.length; i++) h = (h * 31 + nome.charCodeAt(i)) >>> 0;
    return CORES[h % CORES.length];
  }

  /* ---------- Cliente da API ---------- */
  function request(url, opts) {
    return fetch(url, opts).then(function (res) {
      if (res.status === 204) return null;
      return res.json().catch(function () { return null; }).then(function (body) {
        if (!res.ok) {
          var err = new Error((body && body.mensagem) || "Erro " + res.status);
          err.status = res.status;
          err.campos = body && body.campos;
          throw err;
        }
        return body;
      });
    });
  }

  var Api = {
    listar: function () { return request(API); },                                  // GET    /api/vagas
    buscar: function (id) { return request(API + "/" + id); },                     // GET    /api/vagas/{id}
    criar: function (v) { return request(API, json("POST", v)); },                 // POST   /api/vagas
    atualizar: function (id, v) { return request(API + "/" + id, json("PUT", v)); }, // PUT  /api/vagas/{id}
    excluir: function (id) { return request(API + "/" + id, { method: "DELETE" }); } // DELETE /api/vagas/{id}
  };

  function json(method, body) {
    return { method: method, headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) };
  }

  /* ---------- Listagem ---------- */
  function carregar() {
    return Api.listar().then(function (vagas) {
      render(vagas);
    }).catch(function () {
      lista.replaceChildren();
      var box = el("div", "empty");
      box.appendChild(el("p", null, "Não foi possível carregar as vagas. Verifique se a API está em execução."));
      var b = el("button", "btn sm", "Tentar novamente");
      b.type = "button";
      b.addEventListener("click", carregar);
      box.appendChild(b);
      lista.appendChild(box);
      $("count").textContent = "";
    });
  }

  function render(vagas) {
    $("count").textContent = vagas.length === 1 ? "1 vaga cadastrada" : vagas.length + " vagas cadastradas";
    lista.replaceChildren();

    if (!vagas.length) {
      var box = el("div", "empty");
      box.appendChild(el("p", null, "Nenhuma vaga cadastrada ainda."));
      lista.appendChild(box);
      return;
    }

    vagas.forEach(function (v) {
      var card = el("article", "job");

      var logo = el("div", "co-logo", v.empresa.charAt(0).toUpperCase());
      logo.style.background = cor(v.empresa);
      logo.setAttribute("aria-hidden", "true");

      var info = el("div");
      info.appendChild(el("h3", null, v.titulo));
      info.appendChild(el("p", "job-co", v.empresa));
      var tags = el("div", "tags");
      [v.area, v.cidade, v.modalidade, v.tipoContratacao].forEach(function (t) {
        tags.appendChild(el("span", "tag", t));
      });
      info.appendChild(tags);

      var side = el("div", "job-side");
      side.appendChild(el("span", "sal", v.salario || "A combinar"));
      var acoes = el("div", "job-actions");
      acoes.appendChild(botao("Detalhes", "btn ghost sm", function () { abrirDetalhe(v.id); }));
      acoes.appendChild(botao("Editar", "btn ghost sm", function () { abrirForm(v.id); }));
      acoes.appendChild(botao("Excluir", "btn ghost sm", function () { abrirExcluir(v); }));
      side.appendChild(acoes);

      card.append(logo, info, side);
      lista.appendChild(card);
    });
  }

  function botao(texto, cls, fn) {
    var b = el("button", cls, texto);
    b.type = "button";
    b.addEventListener("click", fn);
    return b;
  }

  /* ---------- Detalhe (GET por id) ---------- */
  function abrirDetalhe(id) {
    Api.buscar(id).then(function (v) {
      $("d-titulo").textContent = v.titulo;
      $("d-empresa").textContent = v.empresa;
      $("d-descricao").textContent = v.descricao;
      $("d-salario").textContent = v.salario || "A combinar";
      var tags = $("d-tags");
      tags.replaceChildren();
      [v.area, v.cidade, v.modalidade, v.tipoContratacao].forEach(function (t) {
        tags.appendChild(el("span", "tag", t));
      });
      $("dlg-detalhe").showModal();
    }).catch(function (e) {
      toast(e.message, true);
      if (e.status === 404) carregar();
    });
  }

  /* ---------- Formulário (POST / PUT) ---------- */
  var form = $("form");
  var CAMPOS = { titulo: "f-titulo", empresa: "f-empresa", area: "f-area", cidade: "f-cidade",
                 modalidade: "f-modalidade", tipoContratacao: "f-tipo", salario: "f-salario", descricao: "f-descricao" };

  function limparErros() {
    $("form-erro").hidden = true;
    form.querySelectorAll(".err").forEach(function (s) { s.textContent = ""; });
  }

  function preencher(v) {
    Object.keys(CAMPOS).forEach(function (k) { $(CAMPOS[k]).value = v ? (v[k] || "") : ""; });
    $("f-id").value = v ? v.id : "";
  }

  function abrirForm(id) {
    limparErros();
    if (id == null) {
      preencher(null);
      $("f-titulo-dlg").textContent = "Nova vaga";
      $("dlg-form").showModal();
      return;
    }
    Api.buscar(id).then(function (v) {
      preencher(v);
      $("f-titulo-dlg").textContent = "Editar vaga";
      $("dlg-form").showModal();
    }).catch(function (e) {
      toast(e.message, true);
      if (e.status === 404) carregar();
    });
  }

  function mostrarErroCampo(nome, msg) {
    var input = $(CAMPOS[nome]);
    if (input) input.parentElement.querySelector(".err").textContent = msg;
  }

  form.addEventListener("submit", function (ev) {
    ev.preventDefault();
    limparErros();

    var dados = {};
    var invalido = false;
    Object.keys(CAMPOS).forEach(function (k) {
      var input = $(CAMPOS[k]);
      dados[k] = input.value.trim();
      if (input.required && !dados[k]) {
        mostrarErroCampo(k, "Campo obrigatório");
        invalido = true;
      }
    });
    if (invalido) return;

    var id = $("f-id").value;
    var btn = $("f-salvar");
    btn.disabled = true;

    (id ? Api.atualizar(id, dados) : Api.criar(dados)).then(function () {
      $("dlg-form").close();
      toast(id ? "Vaga atualizada." : "Vaga criada.");
      return carregar();
    }).catch(function (e) {
      if (e.campos) {
        Object.keys(e.campos).forEach(function (k) { mostrarErroCampo(k, e.campos[k]); });
      } else {
        $("form-erro").textContent = e.message;
        $("form-erro").hidden = false;
      }
    }).then(function () { btn.disabled = false; });
  });

  /* ---------- Exclusão (DELETE) ---------- */
  function abrirExcluir(v) {
    vagaParaExcluir = v;
    $("x-texto").textContent = "A vaga “" + v.titulo + "” (" + v.empresa + ") será removida permanentemente.";
    $("dlg-excluir").showModal();
  }

  $("x-confirmar").addEventListener("click", function () {
    if (!vagaParaExcluir) return;
    Api.excluir(vagaParaExcluir.id).then(function () {
      toast("Vaga excluída.");
    }).catch(function (e) {
      toast(e.message, true);
    }).then(function () {
      $("dlg-excluir").close();
      vagaParaExcluir = null;
      return carregar();
    });
  });

  /* ---------- Eventos gerais ---------- */
  $("btn-nova").addEventListener("click", function () { abrirForm(null); });

  document.querySelectorAll("dialog").forEach(function (d) {
    d.querySelectorAll("[data-close]").forEach(function (b) {
      b.addEventListener("click", function () { d.close(); });
    });
  });

  carregar();
})();
