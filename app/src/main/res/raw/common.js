(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');
var nextBtn = document.querySelector('#NextBtn');


function callback(mutations, obs) {
  //obs.disconnect();
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes') {

      if (verifyBtn !== null && mutation.attributeName === 'style' && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
      }
      if (nextBtn !== null && mutation.attributeName === 'style' && nextBtn.style.display !== 'none') {
            nextBtn.click();
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && linkBtn.classList.value==='btn btn-primary rounded get-link xclude-popad' && linkBtn.innerHTML=="Get Link") {
              setTimeout(function() {
                  linkBtn.click();
              }, 3000);
       }
    }
  });
}

var observer = new MutationObserver(callback);


if(verifyBtn !== null){
JSBridge.showToast(verifyBtn.innerHTML);
observer.observe(verifyBtn, { attributes: true });
}

if(linkBtn !== null){
 observer.observe(linkBtn, { attributes: true });
     linkBtn.addEventListener("click", function() {
        var JSBridgeVisited = document.querySelector('#JSBridgeVisited');
        if(JSBridgeVisited==undefined){
            var x = document.createElement("input");
            x.setAttribute("id", "JSBridgeVisited");
            x.setAttribute("type", "hidden");
            linkBtn.append(x);
            var get_link = document.querySelector('.get-link');
            get_link.click();
            JSBridge.setCompleted();
        }
     });
}

})();