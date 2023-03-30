(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');

var isCompleted = true;
var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes' && (mutation.attributeName === 'style' || mutation.attributeName === 'class') ) {
      if (verifyBtn !== null && verifyBtn.style.display !== 'none') {
            verifyBtn.click();
        observer.disconnect();
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && !linkBtn.classList.contains('disabled')) {
              linkBtn.click();
              if(isCompleted){
                 JSBridge.setCompleted();
                 isCompleted = false;
              }
              observer.disconnect();
       }
    }
  });
});


if(verifyBtn !== null){
JSBridge.showToast(verifyBtn.innerHTML);
observer.observe(verifyBtn, { attributes: true });
verifyBtn.addEventListener("click", function() {
setTimeout(function() {
   var nextBtn = document.querySelector('#NextBtn');
   var href = nextBtn.getAttribute('href');
   JSBridge.showToast(href);
   nextBtn.click();
   //window.location = 'https://www.example.com';
}, 5000);
});
}

if(linkBtn !== null){
 observer.observe(linkBtn, { attributes: true });
}

})();