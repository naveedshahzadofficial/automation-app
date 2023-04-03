(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');


function callback(mutations, obs) {
  obs.disconnect();
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && (mutation.attributeName === 'style' || mutation.attributeName === 'class') ) {
      if (verifyBtn !== null && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && linkBtn.classList.value==='btn btn-primary rounded get-link xclude-popad' && linkBtn.innerHTML=="Get Link") {
              if(!linkBtn.classList.contains('visited')){
                  setTimeout(function() {
                  linkBtn.click();
                  setTimeout(function() {
                                    linkBtn.click();
                                    }, 3000);
                  }, 5000);
              }
       }
    }
  });
}

var observer = new MutationObserver(callback);


if(verifyBtn !== null){
JSBridge.showToast(verifyBtn.innerHTML);
observer.observe(verifyBtn, { attributes: true });
verifyBtn.addEventListener("click", function() {
setTimeout(function() {
   var nextBtn = document.querySelector('#NextBtn');
   if(nextBtn === null){
       verifyBtn.click();
   }else{
       var href = nextBtn.getAttribute('href');
       JSBridge.showToast(href);
       nextBtn.click();
   }
}, 5000);
});
}

if(linkBtn !== null){
 observer.observe(linkBtn, { attributes: true });
     linkBtn.addEventListener("click", function() {
        if(!linkBtn.classList.contains('visited')){
                         linkBtn.classList.add('visited');
                         JSBridge.setCompleted();
                      }
     });
}

})();